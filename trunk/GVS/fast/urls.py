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
from django.conf.urls.defaults import patterns

urlpatterns = patterns('fast.views',
    (r'^$', 'index'),
    (r'^register$', 'register'),
    (r'^signup$', 'signup'),
    (r'^confirm/(?P<activation_key>\w+)$', 'confirm'),
    (r'^debugger[/]?$', 'buildingblock_debugger'),
    (r'^formdebugger[/]?$', 'form_debugger'),
    # Comment on production
    (r'^unittests/(?P<test_name>\w+)$', 'unittest'),
)
