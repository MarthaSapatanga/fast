/*
 * GWT compliant generated by Fujaba - CodeGen2 version 03.12.2009
 */

package fast.common.client;

import de.uni_kassel.webcoobra.client.CoobraRoot;
import fujaba.web.runtime.client.CObject;
import fujaba.web.runtime.client.reflect.*;
import fujaba.web.runtime.client.*;
import java.util.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;


 //test

public class FastObject extends CObject implements PropertyChangeClient
{

   public void removeAllFrom(String className) 
   {
   }
   
   /**
   Return ArrayList of all atrr names
   */
   public java.util.ArrayList arrayListOfAttrNames() 
   {
      java.util.ArrayList vec = new java.util.ArrayList();
   	
      return vec;
   }

   // generic set and get methods
   public void set (String fieldName, Object value)
   {
   }  

   public void add (String fieldName, Object value)
   {
      set (fieldName, value);
   }

   public Object get (String fieldName)
   {
      return null;
   }
	protected final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String property, PropertyChangeListener listener)
	{
		getPropertyChangeSupport().addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener listener)
	{
		getPropertyChangeSupport().removePropertyChangeListener(property, listener);
	}

	public PropertyChangeSupport getPropertyChangeSupport()
	{
		return listeners;
	}

   public  FastObject ()
   { 
      boolean fujaba__Success = false;
      CoobraRoot coobra = null;

      // story pattern successor
      try 
      {
         fujaba__Success = false; 

         // create object coobra
         coobra = CoobraRoot.get();

         // create link sharedObjects from coobra to this
         coobra.addToSharedObjects (this);

         fujaba__Success = true;
      }
      catch ( JavaSDMException fujaba__InternalException )
      {
         fujaba__Success = false;
      }

      return ;
   }

   public void removeYou()
   {
   }
}

