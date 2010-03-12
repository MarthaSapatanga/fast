/*
 * GWT compliant generated by Fujaba - CodeGen2 version 03.12.2009
 */

package fast.facttool.client;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Button;
import java.util.*;
import fujaba.web.runtime.client.CObject;
import fujaba.web.runtime.client.reflect.*;
import fujaba.web.runtime.client.*;
import java.util.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;


 //test

public class DeleteAttributeButton
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

   // create attributes for all objects in all states of this statechart
   private Button deleteButton;
   private Panel child;
   private Iterator fujaba__IterParentToChild;
   private Object _TmpObject;

   public void start()
   {
      initStateChart();
      // call doAction on initial state
      build.doAction();
   }

   public void start(FAction parent)
   {
      initStateChart();
      //set parent of rootState
      build.setToParent(parent);
      // call doAction on rootState
      build.doAction();
   }

   // GWT statechart code
   public void initStateChart()
   {
      if(build != null)
         return;

      build = new Build ();
      deleteHandler = new DeleteHandler ();
      remove = new Remove ();
      // NONE

      //deleteHandler.addToFollowers("auto", remove);
      // NONE

      //build.addToFollowers("deleteButton.click", deleteHandler);
   }


   private Build build;
   public class Build extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object parent is really bound
            JavaSDM.ensure ( parent != null );
            // create object deleteButton
            deleteButton = new Button ( );

            // assign attribute deleteButton
            deleteButton.setText ("delete");
            // create link widget from parent to deleteButton
            parent.add (deleteButton);

            fujaba__Success = true;
         }
         catch ( JavaSDMException fujaba__InternalException )
         {
            fujaba__Success = false;
         }


         deleteButton.addClickHandler(deleteHandler);

       }

   }

   private DeleteHandler deleteHandler;
   public class DeleteHandler extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object parent is really bound
            JavaSDM.ensure ( parent != null );
            // iterate to-many link widget from parent to child
            fujaba__Success = false;
            fujaba__IterParentToChild = parent.iterator ();

            while ( fujaba__IterParentToChild.hasNext () )
            {
               try
               {
                  _TmpObject =  fujaba__IterParentToChild.next ();

                  // ensure correct type and really bound of object child
                  JavaSDM.ensure ( _TmpObject instanceof Panel );
                  child = (Panel) _TmpObject;

                  // check isomorphic binding between objects parent and child
                  JavaSDM.ensure ( !parent.equals (child) );

                  // destroy link widget from parent to child
                  parent.remove (child);

                  fujaba__Success = true;
               }
               catch ( JavaSDMException fujaba__InternalException )
               {
                  fujaba__Success = false;
               }
            }
            JavaSDM.ensure (fujaba__Success);
            fujaba__Success = true;
         }
         catch ( JavaSDMException fujaba__InternalException )
         {
            fujaba__Success = false;
         }



   		 if( autoGuardDeleteHandler1 == null)
   		 {
   			 autoGuardDeleteHandler1 = new AutoGuardDeleteHandler1();
   			 autoGuardDeleteHandler1.setSource(deleteHandler);
   			 autoGuardDeleteHandler1.setTarget(remove);
   			 deleteHandler.addToAutoTransitions(autoGuardDeleteHandler1.toString(), autoGuardDeleteHandler1);
   		 }

   		 doAuto();
       }

      private AutoGuardDeleteHandler1 autoGuardDeleteHandler1;
      private class AutoGuardDeleteHandler1 extends FGuard
      {
      }

   }

   private Remove remove;
   public class Remove extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object object is really bound
            JavaSDM.ensure ( object != null );
            // check object parent is really bound
            JavaSDM.ensure ( parent != null );
            // collabStat call
            object.removeYou();
            // collabStat call
            parent.removeFromParent();
            fujaba__Success = true;
         }
         catch ( JavaSDMException fujaba__InternalException )
         {
            fujaba__Success = false;
         }



       }

   }

   // my style test for method.vm


   // my style test for method.vm

   public void start (Panel parent , CObject object )
   { 
      // copy parameters to attributes
      this.parent = parent;
      this.object = object;
      start();
   }

   // attributes for action container method parameters 
   public Panel parent;
   public CObject object;


   public void removeYou()
   {
   }
}

