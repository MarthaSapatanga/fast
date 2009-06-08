from django.shortcuts import render_to_response,get_object_or_404
from django.contrib.auth.decorators import login_required
from django.template import RequestContext
from django.contrib.auth.models import User
from django.http import HttpResponseServerError, HttpResponseRedirect
from django import forms
from django.utils.translation import ugettext as _
import datetime, random, sha
from django.core.mail import EmailMessage
from django.conf import settings
from django.db import transaction
from user.models import UserProfile
from string import split, lower


class RegisterForm(forms.Form):
    username = forms.CharField(max_length=30, required=True)
    email = forms.EmailField(max_length=75, required=True)
    password = forms.CharField(max_length=128, required=True, widget=forms.PasswordInput(render_value=False))
    confirm_password = forms.CharField(max_length=128, required=True, widget=forms.PasswordInput(render_value=False))
    first_name = forms.CharField(max_length=30, required=False)
    last_name = forms.CharField(max_length=30, required=False)
    
    def clean_username(self):
        username = self.cleaned_data["username"]
        try:
            User.objects.get(username=username)
        except User.DoesNotExist:
            return username
        self.errors["username"] = forms.util.ErrorList([_("A user with that username already exists")])
    def clean_password(self):
        if (self.data["password"] == self.data['confirm_password']):
            return self.cleaned_data["password"]
        self.errors["password"] = forms.util.ErrorList([_("The passwords do not match")])


@login_required
def index(request):

    return render_to_response('index.html',{}, 
                  context_instance=RequestContext(request))
    
def register(request):
    form = RegisterForm()
    return render_to_response('registration/register.html',{'form': form,}, 
                  context_instance=RequestContext(request))

@transaction.commit_manually
def signup(request):
    try:    
        if request.method == 'POST': 
            form = RegisterForm(request.POST) # A form bound to the POST data
            form.full_clean()
            if form.is_valid():
                user = User.objects.create_user(form.cleaned_data['username'], form.cleaned_data['email'], form.cleaned_data['password'])
                user.first_name = form.cleaned_data['first_name']
                user.last_name = form.cleaned_data['last_name']
                
                user.save()
                
                if settings.REGISTRATION_CONFIRMATION:
                    user.is_active = False
                    
                    salt = sha.new(str(random.random())).hexdigest()[:5]
                    activation_key = sha.new(salt+user.username).hexdigest()
                    key_expires = datetime.datetime.today() + datetime.timedelta(settings.ACCOUNT_ACTIVATION_DAYS)                                                                                                                         
                    new_profile = UserProfile(user=user,activation_key=activation_key,key_expires=key_expires)
                    new_profile.save()

                    confirm_url =  ('%s://%s/confirm/%s') % (lower(split(request.META['SERVER_PROTOCOL'],'/')[0]), request.META['HTTP_HOST'], new_profile.activation_key)
                    email_subject = _("Your new FAST account confirmation")
                    email_body = _("Hello, %s, and thanks for signing up for an FAST account.\n\nTo activate your account, click this link within %s days:\n\n%s") % (user.username, settings.ACCOUNT_ACTIVATION_DAYS, confirm_url)
                    
                    email = EmailMessage(subject=email_subject, body=email_body, from_email=settings.REGISTRATION_SENDER, to = [user.email])
                    email.send()
                
                    user.save()

                transaction.commit()
                return HttpResponseRedirect('/accounts/login/?next=/')
            else:
                return render_to_response('registration/register.html',{'form': form,}, 
                  context_instance=RequestContext(request))
    except Exception, e:
        transaction.rollback()
        return render_to_response('registration/register.html',{'form': RegisterForm(),}, 
                  context_instance=RequestContext(request))

@transaction.commit_on_success
def confirm(request, activation_key):
    if request.user.is_authenticated():
        return render_to_response('index.html',{}, context_instance=RequestContext(request))
    user_profile = get_object_or_404(UserProfile,
                                     activation_key=activation_key)
    if user_profile.key_expires < datetime.datetime.today():
         return render_to_response('registration/login.html',{'expired': True}, 
                  context_instance=RequestContext(request))
    user_account = user_profile.user
    user_account.is_active = True
    user_account.save()
    return HttpResponseRedirect('/accounts/login/?next=/')


