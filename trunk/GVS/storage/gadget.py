#...............................licence...........................................#
#
#    (C) Copyright 2011 FAST Consortium
#
#     This file is part of FAST Platform.
#
#     FAST Platform is free software: you can redistribute it and/or modify
#     it under the terms of the GNU Affero General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     FAST Platform is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU Affero General Public License for more details.
#
#     You should have received a copy of the GNU Affero General Public License
#     along with FAST Platform.  If not, see <http://www.gnu.org/licenses/>.
#
#     Info about members and contributors of the FAST Consortium
#     is available at
#
#     http://fast.morfeo-project.eu
#
#...............................licence...........................................#
from django.conf import settings
from django.template import RequestContext, Template, Context, loader
from os import path, mkdir
from commons.utils import notEmptyValueOrDefault

def getEzWebTemplate(gadgetData):
    metadata = gadgetData['metadata']
    ezWebContext = Context({'gadgetUri': metadata['gadgetUri'],
                            'gadgetTitle': metadata['name'],
                            'gadgetVendor': metadata['vendor'],
                            'gadgetVersion': metadata['version'],
                            'gadgetAuthor': notEmptyValueOrDefault(metadata, 'authorName', settings.DEFAULT_EZWEB_AUTHOR_NAME),
                            'gadgetMail': notEmptyValueOrDefault(metadata, 'email', settings.DEFAULT_EZWEB_AUTHOR_EMAIL),
                            'gadgetDescription': notEmptyValueOrDefault(metadata, 'description', metadata['name']),
                            'gadgetImageURI': notEmptyValueOrDefault(metadata, 'imageURI', settings.DEFAULT_GADGET_IMAGE_URI),
                            'gadgetWikiURI': notEmptyValueOrDefault(metadata, 'gadgetHomepage', settings.DEFAULT_GADGET_HOMEPAGE_URI),
                            'gadgetHeight': notEmptyValueOrDefault(metadata, 'height', settings.DEFAULT_EZWEB_GADGET_HEIGHT),
                            'gadgetWidth': notEmptyValueOrDefault(metadata, 'width', settings.DEFAULT_EZWEB_GADGET_WIDTH),
                            'gadgetPersistent': metadata['persistent'],
                            'gadgetSlots': gadgetData['prec'],
                            'gadgetEvents': gadgetData['post']})
    return loader.render_to_string(path.join('resources','gadget','ezwebTemplate.xml'), ezWebContext);

def getEzWebHTML(gadgetData):
    metadata = gadgetData['metadata']
    ezWebContext = Context({'platform': 'ezweb',
                            'gadgetUri': metadata['gadgetUri'],
                            'gadgetTitle': metadata['name'],
                            'gadgetPersistent': metadata['persistent'],
                            'gadgetSlots': gadgetData['prec'],
                            'gadgetEvents': gadgetData['post'],
                            'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','ezwebTemplate.html'), ezWebContext);

def getGoogleTemplate(gadgetData):
    metadata = gadgetData['metadata']
    googleContext = Context({'platform': 'google',
                              'gadgetUri': metadata['gadgetUri'],
                              'gadgetTitle': metadata['name'],
                              'gadgetVendor': notEmptyValueOrDefault(metadata, 'vendor', metadata['owner']),
                              'gadgetMail': notEmptyValueOrDefault(metadata, 'email', settings.DEFAULT_EZWEB_AUTHOR_EMAIL),
                              'gadgetDescription': notEmptyValueOrDefault(metadata, 'description', metadata['name']),
                              'gadgetHeight': notEmptyValueOrDefault(metadata, 'height', settings.DEFAULT_EZWEB_GADGET_HEIGHT),
                              'gadgetWidth': notEmptyValueOrDefault(metadata, 'width', settings.DEFAULT_EZWEB_GADGET_WIDTH),
                              'gadgetPersistent': metadata['persistent'],
                              'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','googleTemplate.xml'), googleContext);

def getPlayerHTML(gadgetData, gadgetUri):
    metadata = gadgetData['metadata']

    playerContext = Context({'gadgetUri': gadgetUri,
                             'gadgetSlots': gadgetData['prec'],
                             'gadgetEvents': gadgetData['post'],
                             'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','playerTemplate.html'), playerContext);

def getUnboundHTML(gadgetData, gadgetUri):
    metadata = gadgetData['metadata']

    unboundContext = Context({'gadgetUri': gadgetUri,
                             'gadgetSlots': gadgetData['prec'],
                             'gadgetEvents': gadgetData['post'],
                             'gadgetScreens': gadgetData['screens']})
    return loader.render_to_string(path.join('resources','gadget','unboundTemplate.html'), unboundContext);
