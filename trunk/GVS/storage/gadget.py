from django.conf import settings
from django.template import RequestContext, Template, Context, loader
from os import path, mkdir
from commons.utils import notEmptyValueOrDefault

def getEzWebTemplate(gadgetData):
    metadata = gadgetData['metadata']
    ezWebContext = Context({'gadgetUri': metadata['gadgetUri'],
                            'gadgetTitle': metadata['name'],
                            'gadgetVendor': notEmptyValueOrDefault(metadata, 'vendor', metadata['owner']),
                            'gadgetVersion': metadata['version'],
                            'gadgetAuthor': notEmptyValueOrDefault(metadata, 'authorName', settings.DEFAULT_EZWEB_AUTHOR_NAME),
                            'gadgetMail': notEmptyValueOrDefault(metadata, 'email', settings.DEFAULT_EZWEB_AUTHOR_EMAIL),
                            'gadgetDescription': notEmptyValueOrDefault(metadata, 'description', metadata['name']),
                            'gadgetImageURI': notEmptyValueOrDefault(metadata, 'imageURI', settings.DEFAULT_GADGET_IMAGE_URI),
                            'gadgetWikiURI': notEmptyValueOrDefault(metadata, 'gadgetHomepage', settings.DEFAULT_GADGET_HOMEPAGE_URI),
                            'gadgetHeight': notEmptyValueOrDefault(metadata, 'height', settings.DEFAULT_EZWEB_GADGET_HEIGHT),
                            'gadgetWidth': notEmptyValueOrDefault(metadata, 'width', settings.DEFAULT_EZWEB_GADGET_WIDTH),
                            'gadgetSlots': gadgetData['prec'],
                            'gadgetEvents': gadgetData['post']})
    return loader.render_to_string(path.join('resources','gadget','ezwebTemplate.xml'), ezWebContext);

def getEzWebHTML(gadgetData):
    metadata = gadgetData['metadata']
    ezWebContext = Context({'platform': 'ezweb',
                            'gadgetUri': metadata['gadgetUri'],
                            'gadgetTitle': metadata['name'],
                            'gadgetSlots': gadgetData['prec'],
                            'gadgetEvents': gadgetData['post'],
                            'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','ezwebTemplate.html'), ezWebContext);

def getIGoogleTemplate(gadgetData):
    metadata = gadgetData['metadata']
    igoogleContext = Context({'platform': 'igoogle',
                              'gadgetUri': metadata['gadgetUri'],
                              'gadgetTitle': metadata['name'],
                              'gadgetVendor': notEmptyValueOrDefault(metadata, 'vendor', metadata['owner']),
                              'gadgetMail': notEmptyValueOrDefault(metadata, 'email', settings.DEFAULT_EZWEB_AUTHOR_EMAIL),
                              'gadgetDescription': notEmptyValueOrDefault(metadata, 'description', metadata['name']),
                              'gadgetHeight': notEmptyValueOrDefault(metadata, 'height', settings.DEFAULT_EZWEB_GADGET_HEIGHT),
                              'gadgetWidth': notEmptyValueOrDefault(metadata, 'width', settings.DEFAULT_EZWEB_GADGET_WIDTH),
                              'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','igoogleTemplate.xml'), igoogleContext);

def getPlayerHTML(gadgetData):
    metadata = gadgetData['metadata']

    playerContext = Context({'gadgetUri': settings.MEDIA_URL,
                             'gadgetSlots': gadgetData['prec'],
                             'gadgetEvents': gadgetData['post'],
                             'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','playerTemplate.html'), playerContext);
