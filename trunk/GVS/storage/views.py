from django.conf import settings
from django.utils import simplejson
from django.http import HttpResponse, HttpResponseServerError, Http404
from django.shortcuts import get_object_or_404, get_list_or_404
from django.db import transaction
from gadget import getEzWebTemplate, getEzWebHTML, getIGoogleTemplate, getPlayerHTML
from commons import resource, unzip
from commons.utils import json_encode, valueOrDefault, valueOrEmpty, notEmptyValueOrDefault
from commons.authentication import get_user_authentication
from os import path, mkdir, remove
import zipfile, shutil, tempfile, binascii
from python_rest_client.restful_lib import Connection, isValidResponse
from storage.models import Storage
from buildingblock.models import Screen, Screenflow, BuildingBlockCode

STORAGE_DIR = path.join(settings.BASEDIR, 'static')
GADGET_ZIP_DIR = path.join(settings.BASEDIR, 'media/gadget')
STORAGE_GADGET_ZIP_NAME = 'gadget.zip'

class GadgetStorage(resource.Resource):
    def read(self, request):
        try:
            user = get_user_authentication(request)

            storage = []
            if request.GET.has_key('screenflow'):
                storage = get_list_or_404(Storage, screenflow=request.GET['screenflow'])
            else:
                storage = get_list_or_404(Storage, screenflow__author=user)

            list = []
            for element in storage:
                list.append(element.data)
            response = ','.join(list)

            return HttpResponse('[%s]' % response, mimetype='application/json; charset=UTF-8')
        except Http404:
            return HttpResponse(json_encode([]), mimetype='application/json; charset=UTF-8')
        except Exception, e:
            return HttpResponseServerError(json_encode({'message':unicode(e)}), mimetype='application/json; charset=UTF-8')


    @transaction.commit_on_success
    def create(self, request):
        try:
            user = get_user_authentication(request)

            gadgetData = self.__completeGadgetData(request)

            metadata = gadgetData['metadata']

            storage = Storage(name=metadata['name'],
                              owner=metadata['owner'],
                              version=metadata['version'],
                              screenflow = gadgetData['screenflow'])
            storage.save()

            self.__createResourceURI(request, metadata, storage)

            self.__createGadget(gadgetData, storage)

            self.__storeGadget(metadata)

            metadata['creationDate'] = storage.creationDate
            metadata['id'] = storage.pk
            storage.data = json_encode(metadata)
            storage.save()

            dict = {'id': None, 'name': None, 'screenflow_id': None, 'owner': None,
                    'version' : None, 'gadgetUri' : None, 'creationDate': None}

            return HttpResponse(json_encode(metadata, fields=dict), mimetype='application/json; charset=UTF-8')

        except Exception, e:
            transaction.rollback()
            return HttpResponseServerError(json_encode({'message':unicode(e)}), mimetype='application/json; charset=UTF-8')

    def __completeGadgetData(self, request):
        if request.POST.has_key('gadget'):
            json = simplejson.loads(request.POST['gadget'], encoding = 'utf-8')
        else:
            raise Exception ('Gadget parameter expected in screenflow json')

        gadgetData = getGadgetData(request.POST['screenflow'])

        metadata = gadgetData['metadata']
        #Gadget Data
        metadata['label'] = valueOrDefault(json, 'label', {'en-gb': 'FAST Gadget'})
        metadata['name'] = metadata['label']['en-gb']
        metadata['shortname'] =  valueOrEmpty(json, 'owner')
        metadata['owner'] =  valueOrDefault(json, 'owner', 'Morfeo')
        metadata['vendor'] =  valueOrEmpty(json, 'vendor')
        metadata['version'] = notEmptyValueOrDefault(json, 'version', '1.0')
        metadata['description'] = valueOrDefault(json, 'description', {'en-gb': ''})
        metadata['description'] = metadata['label']['en-gb']
        metadata['imageURI'] = valueOrEmpty(json, 'imageURI')
        metadata['gadgetHomepage'] = valueOrEmpty(json, 'gadgetHomepage')
        metadata['height'] = valueOrEmpty(json, 'height')
        metadata['width'] = valueOrEmpty(json, 'width')
        #Author Data
        metadata['authorName'] =  valueOrEmpty(json, 'authorName')
        metadata['email'] = valueOrEmpty(json, 'email')
        metadata['authorHomepage'] = valueOrEmpty(json, 'authorHomepage')

        return gadgetData

    def __createResourceURI(self, request, metadata, storage):
        metadata['gadgetResource'] = None
        if isLocalStorage():
            base_uri = request.build_absolute_uri('/static')
            metadata['gadgetUri'] =  '/'.join([base_uri, str(storage.pk)])
        else:
            conn = Connection(settings.STORAGE_URL)
            body = {
                'Owner': metadata['owner'],
                'Version': metadata['version'],
                'Author':{'AuthorName': metadata['authorName'],
                          'href': metadata['authorHomepage'],
                          'email': metadata['email']
                          },
                'Vendor':{'VendorName': metadata['vendor']},
                'GadgetMetadata':{'GadgetName': metadata['name'],
                                  'GadgetShortName': metadata['shortname'],
                                  'GadgetDescription': metadata['description']
                                  }
                }

            if metadata['height']!='':
                body['GadgetMetadata']['GadgetDefaultHeight'] = metadata['height']
            if metadata['width']!='':
                body['GadgetMetadata']['GadgetDefaultWidth'] = metadata['width']

            result = conn.request_post(resource='/gadgets/metadata', body=json_encode(body) , headers={'Accept':'application/json', 'Content-Type': 'application/json'})
            if not isValidResponse(result):
                raise Exception(result['body'])
            data = simplejson.loads(result['body'])
            metadata['gadgetUri'] = data['GadgetLocationURL']
            metadata['gadgetResource'] = data['ServiceGadgetUri']
            metadata['gadgetDataUri'] = data['GadgetDataUri']


    def __createGadget(self, gadgetData, storage):
        metadata = gadgetData['metadata']
        gadgetRelativePath = str(storage.pk)
        gadgetPath = path.join(STORAGE_DIR, gadgetRelativePath)
        if (not path.isdir(gadgetPath)):
            mkdir(gadgetPath)
        else:
            raise Exception ('Gadget already exists')

        origin = path.join(GADGET_ZIP_DIR, STORAGE_GADGET_ZIP_NAME)
        gadgetZipFileName = path.join(gadgetPath, STORAGE_GADGET_ZIP_NAME)
        shutil.copyfile (origin, gadgetZipFileName)

        zipFile = zipfile.ZipFile(gadgetZipFileName,'a')

        directory_name = tempfile.mkdtemp(dir=gadgetPath)

        #EzWeb
        ezWebTemplate = getEzWebTemplate(gadgetData)
        ezWebTemplateFile = open (path.join(directory_name, 'ezweb.xml'), 'w')
        ezWebTemplateFile.write(ezWebTemplate.encode('utf-8'))
        ezWebTemplateFile.close()
        zipFile.write(ezWebTemplateFile.name,'./ezweb.xml')

        ezWebHTML = getEzWebHTML(gadgetData)
        ezWebHTMLFile = open (path.join(directory_name, 'ezweb.html'), 'w')
        ezWebHTMLFile.write(ezWebHTML.encode('utf-8'))
        ezWebHTMLFile.close()
        zipFile.write(ezWebHTMLFile.name,'./ezweb.html')

        #iGoogle
        igoogleTemplate = getIGoogleTemplate(gadgetData)
        igoogleTemplateFile = open (path.join(directory_name, 'igoogle.xml'), 'w')
        igoogleTemplateFile.write(igoogleTemplate.encode('utf-8'))
        igoogleTemplateFile.close()
        zipFile.write(igoogleTemplateFile.name,'./igoogle.xml')

        shutil.rmtree(directory_name)
        zipFile.close()

        metadata['gadgetRelativePath'] = gadgetRelativePath


    def __storeGadget(self, gadgetMetaData):
        gadgetPath = path.join(STORAGE_DIR, gadgetMetaData['gadgetRelativePath'])
        gadgetZipFileName = path.join(gadgetPath, STORAGE_GADGET_ZIP_NAME)
        #Remote Storage
        if gadgetMetaData.has_key('gadgetDataUri'):
            conn = Connection(gadgetMetaData['gadgetDataUri'])
            if settings.STORAGE_FORMAT == 'base64string':
                f = open(gadgetZipFileName, 'rb')
                data = f.read()
                f.close()
                data = binascii.b2a_base64(data)
                format = settings.STORAGE_FORMAT
            else:
                data = '/'.join([gadgetMetaData['gadgetUri'], STORAGE_GADGET_ZIP_NAME])
                format = 'URL'
            body = {'Data':data,'DataType':format}
            result = conn.request_post(resource='', body=json_encode(body) , headers={'Accept':'application/json', 'Content-Type': 'application/json'})
            if not isValidResponse(result):
                raise Exception(result['body'])
        #Local Storage
        else:
            un = unzip.unzip()
            un.extract(gadgetZipFileName, gadgetPath)
            remove(gadgetZipFileName)



class StorageEntry(resource.Resource):
    def read(self, request, storage_id):
        user = get_user_authentication(request)

        st = get_object_or_404(Storage, id=storage_id)
        return HttpResponse(st.data, mimetype='application/json; charset=UTF-8')


    @transaction.commit_on_success
    def delete(self, request, storage_id):
        try:
            user = get_user_authentication(request)

            st = Storage.objects.get(id=storage_id)
            storage = simplejson.loads(st.data)
            gadgetPath = path.join(STORAGE_DIR, storage['gadgetRelativePath'])

            st.delete()

            if storage['gadgetResource'] != None:
                conn = Connection(storage['gadgetResource'])
                result = conn.request_delete(resource='', headers={'Accept':'application/json'})
                if not isValidResponse(result):
                    raise Exception(result['body'])

            try:
                shutil.rmtree(gadgetPath)
            except Exception, ex:
                pass

            ok = json_encode({'message':'OK'})
            return HttpResponse(ok, mimetype='application/json; charset=UTF-8')
        except Exception, e:
            transaction.rollback()
            return HttpResponseServerError(json_encode({'message':unicode(e)}), mimetype='application/json; charset=UTF-8')


class GadgetPlayer(resource.Resource):
    def read(self, request):
        try:
            user = get_user_authentication(request)

            gadgetData = getGadgetData(request.GET['screenflow'])

            playerHTML = getPlayerHTML(gadgetData)

            return HttpResponse(playerHTML, mimetype='text/html; charset=UTF-8')
        except Http404:
            return HttpResponse(json_encode([]), mimetype='application/json; charset=UTF-8')
        except Exception, e:
            return HttpResponseServerError(json_encode({'message':unicode(e)}), mimetype='application/json; charset=UTF-8')


def getGadgetData(screenflowId):
    gadgetData = {'metadata':{}}
    scrf = get_object_or_404(Screenflow, id=screenflowId)
    gadgetData['screenflow'] = scrf
    screenflow = simplejson.loads(scrf.data)
    definition = screenflow['definition']
    gadgetData['screens'] = []
    if (definition.has_key('screens')):
        scrs = definition['screens']
        for screen in scrs:
            screen = get_object_or_404(Screen, uri=screen['uri'])
            screen = simplejson.loads(screen.data)
            aux = screen['label']
            screen['label'] = aux['en-gb']
            screen['allCode'] = BuildingBlockCode.objects.get(buildingBlock=screen['id']).code
            gadgetData['screens'].append(screen)

    gadgetData['prec'] = definition['preconditions'] if definition.has_key('preconditions') else []
    gadgetData['post'] = definition['postconditions'] if definition.has_key('postconditions') else []
    return gadgetData


def isLocalStorage():
    if settings.STORAGE_URL == None or settings.STORAGE_URL=='':
        return True
    return False