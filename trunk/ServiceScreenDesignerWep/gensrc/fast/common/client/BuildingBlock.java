/*
 * GWT compliant generated by Fujaba - CodeGen2 version 03.12.2009
 */

package fast.common.client;

import fast.common.client.FASTMappingRule;
import java.util.*;
import fast.common.client.FactPort;
import fast.common.client.TemplateParameter;
import fast.common.client.FastObject;
import fujaba.web.runtime.client.reflect.*;
import fujaba.web.runtime.client.*;
import java.util.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;




public class BuildingBlock extends FastObject
{

   public void removeAllFrom(String className) 
   {
      if ("fast.common.client.FactPort".equals(className)){
         removeAllFromPreconditions();
      }
      else      if ("fast.common.client.FactPort".equals(className)){
         removeAllFromPostconditions();
      }
      else      if ("fast.common.client.FASTMappingRule".equals(className)){
         removeAllFromMappingRules();
      }
      else      if ("fast.common.client.TemplateParameter".equals(className)){
         removeAllFromTemplateParameters();
      }
   }
   
   /**
   Return ArrayList of all atrr names
   */
   public java.util.ArrayList arrayListOfAttrNames() 
   {
      java.util.ArrayList vec = new java.util.ArrayList();
      vec.add("name");
   	
      return vec;
   }

   // generic set and get methods
   public void set (String fieldName, Object value)
   {
      // name
      if ("name".equals(fieldName)){
         setName((String) value);
      }  else// preconditions
      if ("preconditions".equals(fieldName)){
         addToPreconditions ((fast.common.client.FactPort) value);
      }  else// postconditions
      if ("postconditions".equals(fieldName)){
         addToPostconditions ((fast.common.client.FactPort) value);
      }  else// mappingRules
      if ("mappingRules".equals(fieldName)){
         addToMappingRules ((fast.common.client.FASTMappingRule) value);
      }  else// templateParameters
      if ("templateParameters".equals(fieldName)){
         addToTemplateParameters ((fast.common.client.TemplateParameter) value);
      }   }  

   public void add (String fieldName, Object value)
   {
      set (fieldName, value);
   }

   public Object get (String fieldName)
   {
      // name
      if ("name".equals(fieldName)){
         return (String) getName();
      }
      else      if ("preconditions".equals(fieldName))
      {
         return iteratorOfPreconditions();
      }
      else      if ("postconditions".equals(fieldName))
      {
         return iteratorOfPostconditions();
      }
      else      if ("mappingRules".equals(fieldName))
      {
         return iteratorOfMappingRules();
      }
      else      if ("templateParameters".equals(fieldName))
      {
         return iteratorOfTemplateParameters();
      }
      return null;
   }

   /**
    * <pre>
    *           0..1     mappingRules     0..*
    * BuildingBlock ------------------------- FASTMappingRule
    *           serviceScreen               mappingRules
    * </pre>
    */
   public static final String PROPERTY_MAPPING_RULES = "mappingRules";

   private FPropHashSet<FASTMappingRule> mappingRules;

   public FPropHashSet<FASTMappingRule> getMappingRules () {
      return mappingRules;
   }

   public boolean addToMappingRules (FASTMappingRule value) {
      boolean changed = false;

      if (value != null)
      {
         if (this.mappingRules == null)
         {
            this.mappingRules = new FPropHashSet<FASTMappingRule> (this, PROPERTY_MAPPING_RULES);

         }
      
         changed = this.mappingRules.add (value);
         if (changed)
         {
            value.setServiceScreen (this);
         }
      
      }
      return changed;
   }

   public BuildingBlock withMappingRules (FASTMappingRule value ) {
         addToMappingRules ( value);
      return this;
   }

   public BuildingBlock withoutMappingRules (FASTMappingRule value) {
      removeFromMappingRules (value);
      return this;
   }

   public boolean removeFromMappingRules (FASTMappingRule value) {
      boolean changed = false;

      if ((this.mappingRules != null) && (value != null))
      {
      
         changed = this.mappingRules.remove (value);
         if (changed)
         {
            value.setServiceScreen (null);
         }
      
      }
      return changed;
   }

   public void removeAllFromMappingRules () {
   
      FASTMappingRule tmpValue;

      if (mappingRules != null) {
         java.util.Vector tempSet = new java.util.Vector(mappingRules);
         Iterator iter = tempSet.iterator ();
      
         while (iter.hasNext ())
         {
            tmpValue = (FASTMappingRule) iter.next ();
            this.removeFromMappingRules (tmpValue);
         }
      } 
   
   }

   public boolean hasInMappingRules (FASTMappingRule value) {
      return ((this.mappingRules != null) &&
              (value != null) &&
              this.mappingRules.contains (value));
   }

   public Iterator iteratorOfMappingRules () {
      return ((this.mappingRules == null)
              ? FEmptyIterator.get ()
              : this.mappingRules.iterator ());

   }

   public int sizeOfMappingRules () {
      return ((this.mappingRules == null)
              ? 0
              : this.mappingRules.size ());
   }

   public static final String PROPERTY_NAME = "name";

   private String name;

   public void setName (String value)
   {
      if ( ! JavaSDM.stringEquals (this.name, value))
      {
         String oldValue = this.name;
         this.name = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME, oldValue, value);
      }
   }

   public BuildingBlock withName (String value)
   {
      setName (value);
      return this;
   }

   public String getName ()
   {
      return this.name;
   }

   /**
    * <pre>
    *           0..1     postconditions     0..*
    * BuildingBlock ------------------------- FactPort
    *           serviceScreen2               postconditions
    * </pre>
    */
   public static final String PROPERTY_POSTCONDITIONS = "postconditions";

   private FPropHashSet<FactPort> postconditions;

   public FPropHashSet<FactPort> getPostconditions () {
      return postconditions;
   }

   public boolean addToPostconditions (FactPort value) {
      boolean changed = false;

      if (value != null)
      {
         if (this.postconditions == null)
         {
            this.postconditions = new FPropHashSet<FactPort> (this, PROPERTY_POSTCONDITIONS);

         }
      
         changed = this.postconditions.add (value);
         if (changed)
         {
            value.setServiceScreen2 (this);
         }
      
      }
      return changed;
   }

   public BuildingBlock withPostconditions (FactPort value ) {
         addToPostconditions ( value);
      return this;
   }

   public BuildingBlock withoutPostconditions (FactPort value) {
      removeFromPostconditions (value);
      return this;
   }

   public boolean removeFromPostconditions (FactPort value) {
      boolean changed = false;

      if ((this.postconditions != null) && (value != null))
      {
      
         changed = this.postconditions.remove (value);
         if (changed)
         {
            value.setServiceScreen2 (null);
         }
      
      }
      return changed;
   }

   public void removeAllFromPostconditions () {
   
      FactPort tmpValue;

      if (postconditions != null) {
         java.util.Vector tempSet = new java.util.Vector(postconditions);
         Iterator iter = tempSet.iterator ();
      
         while (iter.hasNext ())
         {
            tmpValue = (FactPort) iter.next ();
            this.removeFromPostconditions (tmpValue);
         }
      } 
   
   }

   public boolean hasInPostconditions (FactPort value) {
      return ((this.postconditions != null) &&
              (value != null) &&
              this.postconditions.contains (value));
   }

   public Iterator iteratorOfPostconditions () {
      return ((this.postconditions == null)
              ? FEmptyIterator.get ()
              : this.postconditions.iterator ());

   }

   public int sizeOfPostconditions () {
      return ((this.postconditions == null)
              ? 0
              : this.postconditions.size ());
   }

   /**
    * <pre>
    *           0..1     preconditions     0..*
    * BuildingBlock ------------------------- FactPort
    *           serviceScreen               preconditions
    * </pre>
    */
   public static final String PROPERTY_PRECONDITIONS = "preconditions";

   private FPropHashSet<FactPort> preconditions;

   public FPropHashSet<FactPort> getPreconditions () {
      return preconditions;
   }

   public boolean addToPreconditions (FactPort value) {
      boolean changed = false;

      if (value != null)
      {
         if (this.preconditions == null)
         {
            this.preconditions = new FPropHashSet<FactPort> (this, PROPERTY_PRECONDITIONS);

         }
      
         changed = this.preconditions.add (value);
         if (changed)
         {
            value.setServiceScreen (this);
         }
      
      }
      return changed;
   }

   public BuildingBlock withPreconditions (FactPort value ) {
         addToPreconditions ( value);
      return this;
   }

   public BuildingBlock withoutPreconditions (FactPort value) {
      removeFromPreconditions (value);
      return this;
   }

   public boolean removeFromPreconditions (FactPort value) {
      boolean changed = false;

      if ((this.preconditions != null) && (value != null))
      {
      
         changed = this.preconditions.remove (value);
         if (changed)
         {
            value.setServiceScreen (null);
         }
      
      }
      return changed;
   }

   public void removeAllFromPreconditions () {
   
      FactPort tmpValue;

      if (preconditions != null) {
         java.util.Vector tempSet = new java.util.Vector(preconditions);
         Iterator iter = tempSet.iterator ();
      
         while (iter.hasNext ())
         {
            tmpValue = (FactPort) iter.next ();
            this.removeFromPreconditions (tmpValue);
         }
      } 
   
   }

   public boolean hasInPreconditions (FactPort value) {
      return ((this.preconditions != null) &&
              (value != null) &&
              this.preconditions.contains (value));
   }

   public Iterator iteratorOfPreconditions () {
      return ((this.preconditions == null)
              ? FEmptyIterator.get ()
              : this.preconditions.iterator ());

   }

   public int sizeOfPreconditions () {
      return ((this.preconditions == null)
              ? 0
              : this.preconditions.size ());
   }

   /**
    * <pre>
    *           0..1     templateParameters     0..n
    * BuildingBlock ------------------------- TemplateParameter
    *           serviceScreen               templateParameters
    * </pre>
    */

   private transient FLinkedList<TemplateParameter> templateParameters;

   public FLinkedList<TemplateParameter> getTemplateParameters () {
      return templateParameters;
   }

   public boolean addToTemplateParameters (TemplateParameter value) {
      boolean changed = false;

      if (value != null && !this.hasInTemplateParameters (value))
      {
         if (this.templateParameters == null)
         {
            this.templateParameters = new FLinkedList<TemplateParameter> ();

         }
      
         changed = this.templateParameters.add (value);
         if (changed)
         {
            value.setServiceScreen (this);
         }
      
      }
      return changed;
   }

   public BuildingBlock withTemplateParameters (TemplateParameter value ) {
         addToTemplateParameters ( value);
      return this;
   }

   public BuildingBlock withoutTemplateParameters (TemplateParameter value) {
      removeFromTemplateParameters (value);
      return this;
   }

   public boolean removeFromTemplateParameters (TemplateParameter value) {
      boolean changed = false;

      if ((this.templateParameters != null) && (value != null))
      {
      
         changed = this.templateParameters.remove (value);
         if (changed)
         {
            value.setServiceScreen (null);
         }
      
      }
      return changed;
   }

   public void removeAllFromTemplateParameters () {
   
      TemplateParameter tmpValue;

      if (templateParameters != null) {
         java.util.Vector tempSet = new java.util.Vector(templateParameters);
         Iterator iter = tempSet.iterator ();
      
         while (iter.hasNext ())
         {
            tmpValue = (TemplateParameter) iter.next ();
            this.removeFromTemplateParameters (tmpValue);
         }
      } 
   
   }

   public boolean hasInTemplateParameters (TemplateParameter value) {
      return ((this.templateParameters != null) &&
              (value != null) &&
              this.templateParameters.contains (value));
   }

   public ListIterator iteratorOfTemplateParameters () {
      return ((this.templateParameters == null)
              ? FEmptyListIterator.get ()
              : this.templateParameters.listIterator());

   }

   public int sizeOfTemplateParameters () {
      return ((this.templateParameters == null)
              ? 0
              : this.templateParameters.size ());
   }
   public TemplateParameter getFirstOfTemplateParameters ()
   {
      if (templateParameters == null)
      {
         return null;
      }
      else
      {
         if (templateParameters.size() == 0) 	 
         { 	 
            return null; 	 
         }
         return (TemplateParameter) templateParameters.getFirst ();
      }
   }

   public TemplateParameter getLastOfTemplateParameters ()
   {
      if (templateParameters == null)
      {
         return null;
      }
      else
      {
         if (templateParameters.size() == 0) 	 
         { 	 
            return null; 	 
         }
         return (TemplateParameter) templateParameters.getLast ();
      }
   }
   public TemplateParameter getFromTemplateParameters ( int index )
   {
      if (index >= 0 && index < sizeOfTemplateParameters ())
      {
         return (TemplateParameter) this.templateParameters.get (index);
      }
      else
      {
         throw new IllegalArgumentException ("getTemplateParametersAt(" + index + ")" );
      }
   }

   public int indexOfTemplateParameters ( TemplateParameter value )
   {
      return ((this.templateParameters == null)
              ? -1
              : this.templateParameters.indexOf (value));
   }

   public int indexOfTemplateParameters ( TemplateParameter value, int index )
   {
      return ((this.templateParameters == null)
   	       ? -1
   	       : this.templateParameters.indexOf (value, index));
   }

   public int lastIndexOfTemplateParameters ( TemplateParameter value )
   {
      return ((this.templateParameters == null)
               ? -1
               : this.templateParameters.lastIndexOf (value));
   }

   public int lastIndexOfTemplateParameters ( TemplateParameter value, int index )
   {
      return ((this.templateParameters == null)
               ? -1
               : this.templateParameters.lastIndexOf (value, index));
   }

   public boolean isBeforeOfTemplateParameters ( TemplateParameter leftObject, TemplateParameter rightObject)
   {
      if (templateParameters == null)
      {
         return false;
      }
      else
      {
         return templateParameters.isBefore (leftObject, rightObject);
      }
   }

   public boolean isAfterOfTemplateParameters ( TemplateParameter leftObject, TemplateParameter rightObject)
   {
      if (templateParameters == null)
      {
         return false;
      }
      else
      {
         return templateParameters.isAfter (leftObject, rightObject);
      }
   }

   public TemplateParameter getNextOfTemplateParameters ( TemplateParameter object )
   {
      if (templateParameters == null)
      {
         return null;
      }
      else
      {
         return (TemplateParameter) templateParameters.getNextOf (object);
      }
   }

   public TemplateParameter getNextOfTemplateParameters ( TemplateParameter object, int index)
   {
      if (templateParameters == null)
      {
         return null;
      }
      else
      {
         return (TemplateParameter) templateParameters.getNextOf (object, index);
      }
   }

   public TemplateParameter getPreviousOfTemplateParameters ( TemplateParameter object)
   {
      if (templateParameters == null)
      {
         return null;
      }
      else
      {
         return (TemplateParameter) templateParameters.getPreviousOf (object);
      }
   }

   public TemplateParameter getPreviousOfTemplateParameters ( TemplateParameter object, int index )
   {
      if (templateParameters == null)
      {
         return null;
      }
      else
      {
         return (TemplateParameter) templateParameters.getPreviousOf (object, index);
      }
   }

   public boolean addAfterOfTemplateParameters ( TemplateParameter refObject, TemplateParameter value)
   {
      boolean changed = false;
      if (templateParameters != null)
      {
         int index = templateParameters.indexOf (refObject);
         changed = addToTemplateParameters (index+1, value);
      }
      return changed;
   }

   public boolean addBeforeOfTemplateParameters ( TemplateParameter refObject, TemplateParameter value)
   {
      boolean changed = false;
      if (templateParameters != null)
      {
         int index = templateParameters.indexOf (refObject);
         changed = addToTemplateParameters (index, value);
      }
      return changed;
   }

   public boolean addToTemplateParameters (int index, TemplateParameter value)
   {
      boolean changed = false;

      if (value != null)
      {
         if (this.templateParameters == null)
         {
            this.templateParameters = new FLinkedList<TemplateParameter> (); // or FTreeSet () or FLinkedList ()
         }
         int oldIndex = this.indexOfTemplateParameters (value);
         if (oldIndex != index)
         {
            try
            {
            
               if (oldIndex > -1)
               {
                  templateParameters.remove (oldIndex);
               }
               templateParameters.add (index, value);
               if (oldIndex < 0)
               {
                  value.setServiceScreen (this);
               }
               changed = true;
            
            }
            catch (IndexOutOfBoundsException ex)
            {
               return false;
            }
         }
      }
      return changed;
   }

   public boolean setInTemplateParameters (int index, TemplateParameter value)
   {
      boolean changed = false;

      if (value != null)
      {
         if (this.templateParameters == null)
         {
            this.templateParameters = new FLinkedList<TemplateParameter> (); // or FTreeSet () or FLinkedList ()
         }
         int oldIndex = this.indexOfTemplateParameters (value);
         if (oldIndex != index)
         {
            try
            {
            
               TemplateParameter oldValue = (TemplateParameter)this.templateParameters.set (index, value);
               if (oldIndex > -1)
               {
                  this.templateParameters.remove (oldIndex);
               }
               if (oldValue != value)
               {
                  if (oldValue != null)
                  {
                     oldValue.setServiceScreen (null);
                  }
                  if (oldIndex < 0)
                  {
                     value.setServiceScreen (this);
                  }
                  changed = true;
               }
            
            }
            catch (IndexOutOfBoundsException ex)
            {
               return false;
            }
         }
      }
      return changed;
   }

   public boolean removeFromTemplateParameters (int index)
   {
      boolean changed = false;

      if (this.templateParameters != null && (index >= 0 && index < this.templateParameters.size ()))
      {
      
         TemplateParameter tmpValue = (TemplateParameter) this.templateParameters.remove (index);
         if (tmpValue != null)
         {
            tmpValue.setServiceScreen (null);
            changed = true;
         }
      
      }
      return changed;
   }

   public boolean removeFromTemplateParameters (int index, TemplateParameter value)
   {
      boolean changed = false;

      if ((this.templateParameters != null) && (value != null) && 
          (index >= 0 && index < this.templateParameters.size ()))
      {
         TemplateParameter oldValue = (TemplateParameter) this.templateParameters.get (index);
         if (oldValue == value)
         {
         
            changed = this.removeFromTemplateParameters (index);
         
         }
      }
      return changed;
   }

   public ListIterator<? extends TemplateParameter> iteratorOfTemplateParameters ( TemplateParameter  lowerBound )
   {
      ListIterator<TemplateParameter> result = FEmptyListIterator.<TemplateParameter>get ();

      if (templateParameters != null && lowerBound != null)
      {
         int index = templateParameters.indexOf (lowerBound) + 1;
         result = templateParameters.listIterator (index);
      }
      else if (templateParameters != null && lowerBound == null)
      {
         result = templateParameters.listIterator (0);
      }

      return result;
   }

   public ListIterator<? extends TemplateParameter> iteratorOfTemplateParameters (int index)
   {
      return ((this.templateParameters == null)
              ? FEmptyListIterator.<TemplateParameter>get ()
              : this.templateParameters.listIterator (Math.max(0,Math.min(index,this.templateParameters.size ()))));
   }

   public void removeYou()
   {
      this.removeAllFromMappingRules ();
      this.removeAllFromPostconditions ();
      this.removeAllFromPreconditions ();
      this.removeAllFromTemplateParameters ();
      super.removeYou ();
   }
}


