/*
 * GWT compliant generated by Fujaba - CodeGen2 version 03.12.2009
 */

package fast.facttool.client;

import fast.common.client.FactType;
import fast.facttool.client.FactAttrPanel;
import java.util.*;
import fast.common.client.FactAttribute;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.HorizontalPanel;
import fast.facttool.client.AttributeTextBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import fast.facttool.client.DeleteFactTypeButton;
import com.google.gwt.user.client.ui.TextBox;
import fast.common.client.FactExample;
import fast.facttool.client.ExampleValuesPanel;
import fujaba.web.runtime.client.reflect.*;
import fujaba.web.runtime.client.*;
import java.util.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;




public class FactTypePanel
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
   private AttributeTextBox uriTextBox;
   private HorizontalPanel exampleValTreeRootPanel;
   private Label attrTreeRootLabel;
   private FactAttrPanel attrPanel;
   private TreeItem exampleValRootItem;
   private DeleteFactTypeButton deleteButton;
   private AttributeTextBox mnemonicBox;
   private HorizontalPanel captionPanel;
   private Label exampleValTreeRootLabel;
   private TreeItem treeItem;
   private FactAttribute factAttr;
   private TreeItem attrRootItem;
   private HorizontalPanel panel;
   private FactAttribute newAttr;
   private FactExample newExampleVal;
   private HorizontalPanel attrTreeRootPanel;
   private TextBox nameCaption;
   private AttributeTextBox nameBox;
   private Button exampleValAddButton;
   private Iterator fujaba__IterFactTypeToFactAttr;
   private Button attrAddButton;
   private TextBox typeCaption;
   private ExampleValuesPanel exampleValPanel;
   private TreeItem captionItem;

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

      addKids = new AddKids ();
      attrAddButtonHandler = new AttrAddButtonHandler ();
      build = new Build ();
      exampleValAddButtonHandler = new ExampleValAddButtonHandler ();
      expandTree = new ExpandTree ();
      // NONE

      //build.addToFollowers("attrAddButton.click", attrAddButtonHandler);
      // NONE

      //build.addToFollowers("auto", addKids);
      // NONE

      //addKids.addToFollowers("auto", expandTree);
      // NONE

      //build.addToFollowers("exampleValAddButton.click", exampleValAddButtonHandler);
   }


   private AddKids addKids;
   public class AddKids extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object factType is really bound
            JavaSDM.ensure ( factType != null );
            // iterate to-many link factAttributes from factType to factAttr
            fujaba__Success = false;
            fujaba__IterFactTypeToFactAttr = factType.iteratorOfFactAttributes ();

            while ( fujaba__IterFactTypeToFactAttr.hasNext () )
            {
               try
               {
                  factAttr = (FactAttribute) fujaba__IterFactTypeToFactAttr.next ();

                  // check object factAttr is really bound
                  JavaSDM.ensure ( factAttr != null );
                  // create object attrPanel
                  attrPanel = new FactAttrPanel ( );

                  // collabStat call
                  attrPanel.start(factAttr, attrRootItem);

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



   		 if( autoGuardAddKids1 == null)
   		 {
   			 autoGuardAddKids1 = new AutoGuardAddKids1();
   			 autoGuardAddKids1.setSource(addKids);
   			 autoGuardAddKids1.setTarget(expandTree);
   			 addKids.addToAutoTransitions(autoGuardAddKids1.toString(), autoGuardAddKids1);
   		 }

   		 doAuto();
       }

      private AutoGuardAddKids1 autoGuardAddKids1;
      private class AutoGuardAddKids1 extends FGuard
      {
      }

   }

   private AttrAddButtonHandler attrAddButtonHandler;
   public class AttrAddButtonHandler extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object factType is really bound
            JavaSDM.ensure ( factType != null );
            // create object newAttr
            newAttr = new FactAttribute ( );

            // create object attrPanel
            attrPanel = new FactAttrPanel ( );

            // create link factAttributes from factType to newAttr
            factType.addToFactAttributes (newAttr);

            // collabStat call
            attrPanel.start(newAttr, attrRootItem);
            fujaba__Success = true;
         }
         catch ( JavaSDMException fujaba__InternalException )
         {
            fujaba__Success = false;
         }



       }

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

            // check object factType is really bound
            JavaSDM.ensure ( factType != null );
            // check object parent is really bound
            JavaSDM.ensure ( parent != null );
            // create object panel
            panel = new HorizontalPanel ( );

            // create object treeItem
            treeItem = parent.addItem(panel);

            // create object nameBox
            nameBox = new AttributeTextBox ( );

            // create object mnemonicBox
            mnemonicBox = new AttributeTextBox ( );

            // create object uriTextBox
            uriTextBox = new AttributeTextBox ( );

            // create object attrTreeRootPanel
            attrTreeRootPanel = new HorizontalPanel ( );

            // create object attrTreeRootLabel
            attrTreeRootLabel = new Label ( );

            // create object attrAddButton
            attrAddButton = new Button ( );

            // create object attrRootItem
            attrRootItem = treeItem.addItem(attrTreeRootPanel);

            // create object deleteButton
            deleteButton = new DeleteFactTypeButton ( );

            // create object captionPanel
            captionPanel = new HorizontalPanel ( );

            // create object nameCaption
            nameCaption = new TextBox ( );

            // create object typeCaption
            typeCaption = new TextBox ( );

            // create object captionItem
            captionItem = attrRootItem.addItem(captionPanel);

            // create object exampleValTreeRootPanel
            exampleValTreeRootPanel = new HorizontalPanel ( );

            // create object exampleValTreeRootLabel
            exampleValTreeRootLabel = new Label ( );

            // create object exampleValAddButton
            exampleValAddButton = new Button ( );

            // create object exampleValRootItem
            exampleValRootItem = treeItem.addItem(exampleValTreeRootPanel);

            // assign attribute nameBox
            nameBox.setAttrName ("typeName");
            // assign attribute mnemonicBox
            mnemonicBox.setAttrName ("mnemonic");
            // assign attribute uriTextBox
            uriTextBox.setAttrName ("uri");
            // assign attribute attrTreeRootLabel
            attrTreeRootLabel.setText ("Fact Attributes:   ");
            // assign attribute attrAddButton
            attrAddButton.setText ("add");
            // assign attribute attrRootItem
            attrRootItem.setState (true);
            // assign attribute nameCaption
            nameCaption.setText ("name");
            // assign attribute nameCaption
            nameCaption.setEnabled (false);
            // assign attribute typeCaption
            typeCaption.setText ("type");
            // assign attribute typeCaption
            typeCaption.setEnabled (false);
            // assign attribute exampleValTreeRootLabel
            exampleValTreeRootLabel.setText ("Example Values: ");
            // assign attribute exampleValAddButton
            exampleValAddButton.setText ("add");
            // assign attribute exampleValRootItem
            exampleValRootItem.setState (true);
            // create link widget from attrTreeRootPanel to attrTreeRootLabel
            attrTreeRootPanel.add (attrTreeRootLabel);

            // create link widget from attrTreeRootPanel to attrAddButton
            attrTreeRootPanel.add (attrAddButton);

            // create link widget from captionPanel to nameCaption
            captionPanel.add (nameCaption);

            // create link widget from captionPanel to typeCaption
            captionPanel.add (typeCaption);

            // create link widget from exampleValTreeRootPanel to exampleValTreeRootLabel
            exampleValTreeRootPanel.add (exampleValTreeRootLabel);

            // create link widget from exampleValTreeRootPanel to exampleValAddButton
            exampleValTreeRootPanel.add (exampleValAddButton);

            // collabStat call
            nameBox.start(panel, factType);
            // collabStat call
            mnemonicBox.start(panel, factType);
            // collabStat call
            uriTextBox.start(panel, factType);
            // collabStat call
            deleteButton.start(panel, factType, treeItem);
            fujaba__Success = true;
         }
         catch ( JavaSDMException fujaba__InternalException )
         {
            fujaba__Success = false;
         }


         attrAddButton.addClickHandler(attrAddButtonHandler);
         exampleValAddButton.addClickHandler(exampleValAddButtonHandler);

   		 if( autoGuardBuild1 == null)
   		 {
   			 autoGuardBuild1 = new AutoGuardBuild1();
   			 autoGuardBuild1.setSource(build);
   			 autoGuardBuild1.setTarget(addKids);
   			 build.addToAutoTransitions(autoGuardBuild1.toString(), autoGuardBuild1);
   		 }

   		 doAuto();
       }

      private AutoGuardBuild1 autoGuardBuild1;
      private class AutoGuardBuild1 extends FGuard
      {
      }

   }

   private ExampleValAddButtonHandler exampleValAddButtonHandler;
   public class ExampleValAddButtonHandler extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object factType is really bound
            JavaSDM.ensure ( factType != null );
            // create object newExampleVal
            newExampleVal = new FactExample ( );

            // create object exampleValPanel
            exampleValPanel = new ExampleValuesPanel ( );

            // create link factExamples from newExampleVal to factType
            newExampleVal.setFactType (factType);

            // collabStat call
            exampleValPanel.start(newExampleVal, exampleValRootItem);
            fujaba__Success = true;
         }
         catch ( JavaSDMException fujaba__InternalException )
         {
            fujaba__Success = false;
         }



       }

   }

   private ExpandTree expandTree;
   public class ExpandTree extends FAction
   {
       public void doAction()
       {
   		 boolean fujaba__Success = false;

         // story pattern storypatternwiththis
         try 
         {
            fujaba__Success = false; 

            // check object treeItem is really bound
            JavaSDM.ensure ( treeItem != null );
            // assign attribute treeItem
            treeItem.setState (true);
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


   // my style test for method.vm


   // my style test for method.vm

   public void start (FactType factType , TreeItem parent )
   { 
      // copy parameters to attributes
      this.factType = factType;
      this.parent = parent;
      start();
   }

   // attributes for action container method parameters 
   public FactType factType;
   public TreeItem parent;


   public void removeYou()
   {
   }
}


