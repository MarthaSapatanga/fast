from commons.authentication import get_user_authentication
from django.db import transaction
from django.core import serializers
from django.http import HttpResponse, HttpResponseServerError
from django.contrib.auth.models import User
from django.utils import simplejson
from user.models import UserProfile
from commons.resource import Resource
from commons.utils import json_encode
from commons.httpUtils import PUT_parameter

class Preferences(Resource):

    def read(self, request):
        user = get_user_authentication(request)
        profile =  UserProfile.objects.get(user=user)
        data = json_encode(profile)
        return HttpResponse(data, mimetype='application/json; charset=UTF-8')
    
    @transaction.commit_on_success
    def update(self, request):
        try:
            user = get_user_authentication(request)
            
            # Get the gadget data from the request
            preferences = simplejson.loads(PUT_parameter(request, 'preferences'))
            
            profile = UserProfile.objects.get(user=user)
            profile.ezweb_url = preferences["ezweb_url"]
            profile.ezweb_username = preferences["ezweb_username"]           
            profile.save()
                
            ok = json_encode({"message":"OK"})

            return HttpResponse(ok, mimetype='application/json; charset=UTF-8')
    
        except Exception, ex:
            return HttpResponseServerError(serializers.serialize("json", {"message":unicode(ex)}), mimetype='application/json; charset=UTF-8')