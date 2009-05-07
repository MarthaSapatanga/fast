from os import path
from django.conf.urls.defaults import patterns, include
from django.conf import settings

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Static content
     (r'^fast/(.*)$', 'django.views.static.serve', {'document_root': path.join(settings.BASEDIR, 'media')}),

    # Static gadgets
     (r'^static/(.*)$', 'django.views.static.serve', {'document_root': path.join(settings.BASEDIR, 'static')}),

    # Fast
    (r'^', include('fast.urls')),

    # Deploy
    (r'^deploy/', include('deploy.urls')),

    #
    (r'^catalogue/', include('catalogue.urls')),

    #Admin interface
    (r'^admin/(.*)', admin.site.root),
)
