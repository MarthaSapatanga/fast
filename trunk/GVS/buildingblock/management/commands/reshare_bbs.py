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
from django.core.management.base import BaseCommand, CommandError
from django.http import HttpResponse
from django.utils import simplejson
from django.conf import settings

from buildingblock.models import BuildingBlock

from python_rest_client.restful_lib import Connection, isValidResponse
from commons.utils import json_encode, cleanUrl
from optparse import make_option

import os
from os import path
import re

class Command(BaseCommand):
    concepts_url = cleanUrl(settings.CATALOGUE_URL) + "/concepts"
    help = "Republish all the building block's into the catalogue"
    option_list = BaseCommand.option_list + (
        make_option('--concepts', '-c',
            action='store',
            dest='concepts_dir',
            type='string',
            help='Publish all the concepts stored in the parameter dir'),
        make_option('--all', '-a',
            action='store_true',
            dest='all',
            help='Share all the building blocks instead the previously shared ones'),
    )


    def handle(self, *args, **options):
        if options["all"]:
            bb_list = BuildingBlock.objects.all()
        else:
            bb_list = BuildingBlock.objects.exclude(uri=None)
        for bb in bb_list:
            bb = bb.child_model()
            if bb.uri != None and bb.uri != '':
                try:
                    print "Unsharing: %s" % bb.pk
                    bb.unshare()
                except Exception, e:
                    print "Cannot unshare: %s. Maybe the catalogue was emptied before?" % bb.pk
                    bb.uri = None
                    bb.save()

            bb.compile_code()
            try:
                print "Sharing %s" % bb.pk
                bb.share()
            except Exception, e:
                print "ERROR sharing: %s" % bb.pk
                print e

        concepts_dir = options["concepts_dir"]
        if concepts_dir:
            if not path.isdir(concepts_dir):
                raise CommandError("'%s' is not a valid directory" % concepts_dir)

            json_pattern = re.compile(r'^.*\.json$')
            for filename in filter(json_pattern.match, os.listdir(concepts_dir)):
                filepath = path.join(concepts_dir, filename)
                print "\nImporting %s..." % filepath,
                f = open(filepath)
                concepts = simplejson.load(f)
                for concept in concepts:
                    conn = Connection(self.concepts_url)
                    body = simplejson.dumps(concept)
                    result = conn.request_post('', body=body, headers={'Accept':'text/json'})
                    if isValidResponse(result):
                        print "OK ",
                    else:
                        print "Error"
                        print result
