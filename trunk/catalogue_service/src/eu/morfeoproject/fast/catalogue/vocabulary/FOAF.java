/**
 * Copyright (c) 2008-2011, FAST Consortium
 * 
 * This file is part of FAST Platform.
 * 
 * FAST Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FAST Platform is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with FAST Platform. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Info about members and contributors of the FAST Consortium
 * is available at http://fast.morfeo-project.eu
 *
 **/
package eu.morfeoproject.fast.catalogue.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.ontoware.rdf2go.util.VocabularyWriter on Mon Apr 20 13:57:12 BST 2009
 * input file: src/eu/morfeoproject/fast/catalogue/ontologies/foaf.rdf
 * namespace: http://xmlns.com/foaf/0.1/
 */
public interface FOAF {
	public static final URI NS_FOAF = new URIImpl("http://xmlns.com/foaf/0.1/",false);

    /**
     * Label: Document 
     * Comment: A document. 
     */
    public static final URI Document = new URIImpl("http://xmlns.com/foaf/0.1/Document", false);

    /**
     * Label: Online E-commerce Account 
     * Comment: An online e-commerce account. 
     */
    public static final URI OnlineEcommerceAccount = new URIImpl("http://xmlns.com/foaf/0.1/OnlineEcommerceAccount", false);

    /**
     * Label: Online Account 
     * Comment: An online account. 
     */
    public static final URI OnlineAccount = new URIImpl("http://xmlns.com/foaf/0.1/OnlineAccount", false);

    /**
     * Label: Person 
     * Comment: A person. 
     */
    public static final URI Person = new URIImpl("http://xmlns.com/foaf/0.1/Person", false);

    /**
     * Label: Group 
     * Comment: A class of Agents. 
     */
    public static final URI Group = new URIImpl("http://xmlns.com/foaf/0.1/Group", false);

    /**
     * Label: Organization 
     * Comment: An organization. 
     */
    public static final URI Organization = new URIImpl("http://xmlns.com/foaf/0.1/Organization", false);

    /**
     * Label: Project 
     * Comment: A project (a collective endeavour of some kind). 
     */
    public static final URI Project = new URIImpl("http://xmlns.com/foaf/0.1/Project", false);

    /**
     * Label: Online Chat Account 
     * Comment: An online chat account. 
     */
    public static final URI OnlineChatAccount = new URIImpl("http://xmlns.com/foaf/0.1/OnlineChatAccount", false);

    /**
     * Label: Image 
     * Comment: An image. 
     */
    public static final URI Image = new URIImpl("http://xmlns.com/foaf/0.1/Image", false);

    /**
     * Label: Agent 
     * Comment: An agent (eg. person, group, software or physical artifact). 
     */
    public static final URI Agent = new URIImpl("http://xmlns.com/foaf/0.1/Agent", false);

    /**
     * Label: Online Gaming Account 
     * Comment: An online gaming account. 
     */
    public static final URI OnlineGamingAccount = new URIImpl("http://xmlns.com/foaf/0.1/OnlineGamingAccount", false);

    /**
     * Label: PersonalProfileDocument 
     * Comment: A personal profile RDF document. 
     */
    public static final URI PersonalProfileDocument = new URIImpl("http://xmlns.com/foaf/0.1/PersonalProfileDocument", false);

    /**
     */
    public static final URI Class = new URIImpl("http://www.w3.org/2000/01/rdf-schema#Class", false);

    /**
     */
    public static final URI SpatialThing = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing", false);

    /**
     */
    public static final URI Agent_3 = new URIImpl("http://xmlns.com/wordnet/1.6/Agent-3", false);

    /**
     * Label: interest_topic 
     * Comment: A thing of interest to this person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI topic_interest = new URIImpl("http://xmlns.com/foaf/0.1/topic_interest", false);

    /**
     * Label: phone 
     * Comment: A phone,  specified using fully qualified tel: URI scheme (refs: http://www.w3.org/Addressing/schemes.html#tel). 
     */
    public static final URI phone = new URIImpl("http://xmlns.com/foaf/0.1/phone", false);

    /**
     * Label: ICQ chat ID 
     * Comment: An ICQ chat ID 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI icqChatID = new URIImpl("http://xmlns.com/foaf/0.1/icqChatID", false);

    /**
     * Label: Yahoo chat ID 
     * Comment: A Yahoo chat ID 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI yahooChatID = new URIImpl("http://xmlns.com/foaf/0.1/yahooChatID", false);

    /**
     * Label: member 
     * Comment: Indicates a member of a Group 
     * Comment: http://xmlns.com/foaf/0.1/Group 
     * Range: http://xmlns.com/foaf/0.1/Agent 
     */
    public static final URI member = new URIImpl("http://xmlns.com/foaf/0.1/member", false);

    /**
     * Label: Given name 
     * Comment: The given name of some person. 
     */
    public static final URI givenname = new URIImpl("http://xmlns.com/foaf/0.1/givenname", false);

    /**
     * Label: birthday 
     * Comment: The birthday of this Agent, represented in mm-dd string form, eg. '12-31'. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI birthday = new URIImpl("http://xmlns.com/foaf/0.1/birthday", false);

    /**
     * Label: image 
     * Comment: An image that can be used to represent some thing (ie. those depictions which are particularly representative of something, eg. one's photo on a homepage). 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Image 
     */
    public static final URI img = new URIImpl("http://xmlns.com/foaf/0.1/img", false);

    /**
     * Label: name 
     * Comment: A name for some thing. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI name = new URIImpl("http://xmlns.com/foaf/0.1/name", false);

    /**
     * Label: maker 
     * Comment: An agent that  made this thing. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://xmlns.com/foaf/0.1/Agent 
     */
    public static final URI maker = new URIImpl("http://xmlns.com/foaf/0.1/maker", false);

    /**
     * Label: tipjar 
     * Comment: A tipjar document for this agent, describing means for payment and reward. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI tipjar = new URIImpl("http://xmlns.com/foaf/0.1/tipjar", false);

    /**
     * Label: membershipClass 
     * Comment: Indicates the class of individuals that are a member of a Group 
     */
    public static final URI membershipClass = new URIImpl("http://xmlns.com/foaf/0.1/membershipClass", false);

    /**
     * Label: account name 
     * Comment: Indicates the name (identifier) associated with this online account. 
     * Comment: http://xmlns.com/foaf/0.1/OnlineAccount 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI accountName = new URIImpl("http://xmlns.com/foaf/0.1/accountName", false);

    /**
     * Label: sha1sum of a personal mailbox URI name 
     * Comment: The sha1sum of the URI of an Internet mailbox associated with exactly one owner, the  first owner of the mailbox. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI mbox_sha1sum = new URIImpl("http://xmlns.com/foaf/0.1/mbox_sha1sum", false);

    /**
     * Label: geekcode 
     * Comment: A textual geekcode for this person, see http://www.geekcode.com/geek.html 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI geekcode = new URIImpl("http://xmlns.com/foaf/0.1/geekcode", false);

    /**
     * Label: interest 
     * Comment: A page about a topic of interest to this person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI interest = new URIImpl("http://xmlns.com/foaf/0.1/interest", false);

    /**
     * Label: depicts 
     * Comment: A thing depicted in this representation. 
     * Comment: http://xmlns.com/foaf/0.1/Image 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI depicts = new URIImpl("http://xmlns.com/foaf/0.1/depicts", false);

    /**
     * Label: knows 
     * Comment: A person known by this person (indicating some level of reciprocated interaction between the parties). 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Person 
     */
    public static final URI knows = new URIImpl("http://xmlns.com/foaf/0.1/knows", false);

    /**
     * Label: homepage 
     * Comment: A homepage for some thing. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI homepage = new URIImpl("http://xmlns.com/foaf/0.1/homepage", false);

    /**
     * Label: firstName 
     * Comment: The first name of a person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI firstName = new URIImpl("http://xmlns.com/foaf/0.1/firstName", false);

    /**
     * Label: Surname 
     * Comment: The surname of some person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI surname = new URIImpl("http://xmlns.com/foaf/0.1/surname", false);

    /**
     * Label: is primary topic of 
     * Comment: A document that this thing is the primary topic of. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI isPrimaryTopicOf = new URIImpl("http://xmlns.com/foaf/0.1/isPrimaryTopicOf", false);

    /**
     * Label: page 
     * Comment: A page or document about this thing. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI page = new URIImpl("http://xmlns.com/foaf/0.1/page", false);

    /**
     * Label: account service homepage 
     * Comment: Indicates a homepage of the service provide for this online account. 
     * Comment: http://xmlns.com/foaf/0.1/OnlineAccount 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI accountServiceHomepage = new URIImpl("http://xmlns.com/foaf/0.1/accountServiceHomepage", false);

    /**
     * Label: depiction 
     * Comment: A depiction of some thing. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://xmlns.com/foaf/0.1/Image 
     */
    public static final URI depiction = new URIImpl("http://xmlns.com/foaf/0.1/depiction", false);

    /**
     * Label: funded by 
     * Comment: An organization funding a project or person. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI fundedBy = new URIImpl("http://xmlns.com/foaf/0.1/fundedBy", false);

    /**
     * Label: title 
     * Comment: Title (Mr, Mrs, Ms, Dr. etc) 
     */
    public static final URI title = new URIImpl("http://xmlns.com/foaf/0.1/title", false);

    /**
     * Label: weblog 
     * Comment: A weblog of some thing (whether person, group, company etc.). 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI weblog = new URIImpl("http://xmlns.com/foaf/0.1/weblog", false);

    /**
     * Label: logo 
     * Comment: A logo representing some thing. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI logo = new URIImpl("http://xmlns.com/foaf/0.1/logo", false);

    /**
     * Label: workplace homepage 
     * Comment: A workplace homepage of some person; the homepage of an organization they work for. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI workplaceHomepage = new URIImpl("http://xmlns.com/foaf/0.1/workplaceHomepage", false);

    /**
     * Label: based near 
     * Comment: A location that something is based near, for some broadly human notion of near. 
     * Comment: http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing 
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing 
     */
    public static final URI based_near = new URIImpl("http://xmlns.com/foaf/0.1/based_near", false);

    /**
     * Label: thumbnail 
     * Comment: A derived thumbnail image. 
     * Comment: http://xmlns.com/foaf/0.1/Image 
     * Range: http://xmlns.com/foaf/0.1/Image 
     */
    public static final URI thumbnail = new URIImpl("http://xmlns.com/foaf/0.1/thumbnail", false);

    /**
     * Label: primary topic 
     * Comment: The primary topic of some page or document. 
     * Comment: http://xmlns.com/foaf/0.1/Document 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI primaryTopic = new URIImpl("http://xmlns.com/foaf/0.1/primaryTopic", false);

    /**
     * Label: AIM chat ID 
     * Comment: An AIM chat ID 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI aimChatID = new URIImpl("http://xmlns.com/foaf/0.1/aimChatID", false);

    /**
     * Label: made 
     * Comment: Something that was made by this agent. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI made = new URIImpl("http://xmlns.com/foaf/0.1/made", false);

    /**
     * Label: work info homepage 
     * Comment: A work info homepage of some person; a page about their work for some organization. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI workInfoHomepage = new URIImpl("http://xmlns.com/foaf/0.1/workInfoHomepage", false);

    /**
     * Label: current project 
     * Comment: A current project this person works on. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI currentProject = new URIImpl("http://xmlns.com/foaf/0.1/currentProject", false);

    /**
     * Label: holds account 
     * Comment: Indicates an account held by this agent. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://xmlns.com/foaf/0.1/OnlineAccount 
     */
    public static final URI holdsAccount = new URIImpl("http://xmlns.com/foaf/0.1/holdsAccount", false);

    /**
     * Label: publications 
     * Comment: A link to the publications of this person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI publications = new URIImpl("http://xmlns.com/foaf/0.1/publications", false);

    /**
     * Label: sha1sum (hex) 
     * Comment: A sha1sum hash, in hex. 
     * Comment: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI sha1 = new URIImpl("http://xmlns.com/foaf/0.1/sha1", false);

    /**
     * Label: gender 
     * Comment: The gender of this Agent (typically but not necessarily 'male' or 'female'). 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI gender = new URIImpl("http://xmlns.com/foaf/0.1/gender", false);

    /**
     * Label: personal mailbox 
     * Comment: A  personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that  there is (across time and change) at most one individual that ever has any particular value for foaf:mbox. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI mbox = new URIImpl("http://xmlns.com/foaf/0.1/mbox", false);

    /**
     * Label: myersBriggs 
     * Comment: A Myers Briggs (MBTI) personality classification. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI myersBriggs = new URIImpl("http://xmlns.com/foaf/0.1/myersBriggs", false);

    /**
     * Label: plan 
     * Comment: A .plan comment, in the tradition of finger and '.plan' files. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI plan = new URIImpl("http://xmlns.com/foaf/0.1/plan", false);

    /**
     * Label: past project 
     * Comment: A project this person has previously worked on. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI pastProject = new URIImpl("http://xmlns.com/foaf/0.1/pastProject", false);

    /**
     * Label: schoolHomepage 
     * Comment: A homepage of a school attended by the person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://xmlns.com/foaf/0.1/Document 
     */
    public static final URI schoolHomepage = new URIImpl("http://xmlns.com/foaf/0.1/schoolHomepage", false);

    /**
     * Label: family_name 
     * Comment: The family_name of some person. 
     * Comment: http://xmlns.com/foaf/0.1/Person 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI family_name = new URIImpl("http://xmlns.com/foaf/0.1/family_name", false);

    /**
     * Label: MSN chat ID 
     * Comment: An MSN chat ID 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI msnChatID = new URIImpl("http://xmlns.com/foaf/0.1/msnChatID", false);

    /**
     * Label: theme 
     * Comment: A theme. 
     * Comment: http://www.w3.org/2002/07/owl#Thing 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI theme = new URIImpl("http://xmlns.com/foaf/0.1/theme", false);

    /**
     * Label: topic 
     * Comment: A topic of some page or document. 
     * Comment: http://xmlns.com/foaf/0.1/Document 
     * Range: http://www.w3.org/2002/07/owl#Thing 
     */
    public static final URI topic = new URIImpl("http://xmlns.com/foaf/0.1/topic", false);

    /**
     * Label: DNA checksum 
     * Comment: A checksum for the DNA of some thing. Joke. 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI dnaChecksum = new URIImpl("http://xmlns.com/foaf/0.1/dnaChecksum", false);

    /**
     * Label: nickname 
     * Comment: A short informal nickname characterising an agent (includes login identifiers, IRC and other chat nicknames). 
     */
    public static final URI nick = new URIImpl("http://xmlns.com/foaf/0.1/nick", false);

    /**
     * Label: jabber ID 
     * Comment: A jabber ID for something. 
     * Comment: http://xmlns.com/foaf/0.1/Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI jabberID = new URIImpl("http://xmlns.com/foaf/0.1/jabberID", false);

}
