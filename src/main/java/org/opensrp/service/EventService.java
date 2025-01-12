package org.opensrp.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.opensrp.common.AllConstants.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.repository.EventsRepository;
import org.opensrp.search.EventSearchBean;
import org.opensrp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
	
	private final EventsRepository allEvents;
	
	private ClientService clientService;
	
	@Autowired
	public EventService(EventsRepository allEvents, ClientService clientService) {
		this.allEvents = allEvents;
		this.clientService = clientService;
	}
	
	public List<Event> findAllByIdentifier(String identifier) {
		return allEvents.findAllByIdentifier(identifier);
	}
	
	public List<Event> findAllByIdentifier(String identifierType, String identifier) {
		return allEvents.findAllByIdentifier(identifierType, identifier);
	}
	
	public Event getById(String id) {
		return allEvents.findById(id);
	}
	
	public Event getByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		return allEvents.findByBaseEntityAndFormSubmissionId(baseEntityId, formSubmissionId);
	}
	
	public List<Event> findByBaseEntityId(String baseEntityId) {
		return allEvents.findByBaseEntityId(baseEntityId);
	}
	
	public Event findByFormSubmissionId(String formSubmissionId) {
		return allEvents.findByFormSubmissionId(formSubmissionId);
	}
	
	public List<Event> findEventsBy(EventSearchBean eventSearchBean) {
		return allEvents.findEvents(eventSearchBean);
	}
	
	public List<Event> findEventsByDynamicQuery(String query) {
		return allEvents.findEventsByDynamicQuery(query);
	}
	
	private static Logger logger = LoggerFactory.getLogger(EventService.class.toString());
	
	public Event find(String uniqueId) {
		try {
			List<Event> el = allEvents.findAllByIdentifier(uniqueId);
			return getUniqueEventFromEventList(el);
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Multiple events with identifier " + uniqueId + " exist.");
		}
	}
	
	public Event find(Event event) {
		for (String idt : event.getIdentifiers().keySet()) {
			try {
				List<Event> el = allEvents.findAllByIdentifier(event.getIdentifier(idt));
				return getUniqueEventFromEventList(el);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
				        "Multiple events with identifier type " + idt + " and ID " + event.getIdentifier(idt) + " exist.");
			}
		}
		return null;
	}
	
	public Event findById(String eventId) {
		try {
			if (eventId == null || eventId.isEmpty()) {
				return null;
			}
			return allEvents.findById(eventId);
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	public synchronized Event addEvent(Event event) {
		Event e = find(event);
		if (e != null) {
			throw new IllegalArgumentException(
			        "An event already exists with given list of identifiers. Consider updating data.[" + e + "]");
		}
		
		if (event.getFormSubmissionId() != null
		        && getByBaseEntityAndFormSubmissionId(event.getBaseEntityId(), event.getFormSubmissionId()) != null) {
			throw new IllegalArgumentException(
			        "An event already exists with given baseEntity and formSubmission combination. Consider updating");
		}
		
		event.setDateCreated(DateTime.now());
		allEvents.add(event);
		return event;
	}
	
	/**
	 * An out of area event is used to record services offered outside a client's catchment area.
	 * The event usually will have a client unique identifier(ZEIR_ID) as the only way to identify
	 * the client.This method finds the client based on the identifier and assigns a basentityid to
	 * the event
	 *
	 * @param event
	 * @return
	 */
	public synchronized Event processOutOfArea(Event event) {
		try {
			final String BIRTH_REGISTRATION_EVENT = "Birth Registration";
			final String GROWTH_MONITORING_EVENT = "Growth Monitoring";
			final String VACCINATION_EVENT = "Vaccination";
			final String OUT_OF_AREA_SERVICE = "Out of Area Service";
			final String NFC_CARD_IDENTIFIER = "NFC_Card_Identifier";
			final String CARD_ID_PREFIX = "c_";
			
			if (StringUtils.isNotBlank(event.getBaseEntityId())) {
				return event;
			}
			
			//get events identifiers; 
			String identifier = event.getIdentifier(Client.ZEIR_ID);
			if (StringUtils.isBlank(identifier)) {
				return event;
			}
			
			boolean isCardId = identifier.startsWith(CARD_ID_PREFIX);
			
			List<org.opensrp.domain.Client> clients =
			        
			        isCardId ? clientService
			                .findAllByAttribute(NFC_CARD_IDENTIFIER, identifier.substring(CARD_ID_PREFIX.length()))
			                : clientService.findAllByIdentifier(Client.ZEIR_ID.toUpperCase(), identifier);
			
			if (clients == null || clients.isEmpty()) {
				return event;
			}
			
			for (org.opensrp.domain.Client client : clients) {
				
				//set providerid to the last providerid who served this client in their catchment (assumption)
				List<Event> existingEvents = findByBaseEntityAndType(client.getBaseEntityId(), BIRTH_REGISTRATION_EVENT);
				
				if (existingEvents == null || existingEvents.isEmpty()) {
					return event;
				}
				
				Event birthRegEvent = existingEvents.get(0);
				event.getIdentifiers().remove(Client.ZEIR_ID.toUpperCase());
				event.setBaseEntityId(client.getBaseEntityId());
				//Map<String, String> identifiers = event.getIdentifiers();
				//event identifiers are unique so removing zeir_id since baseentityid has been found
				//also out of area service events stick with the providerid so that they can sync back to them for reports generation
				if (!event.getEventType().startsWith(OUT_OF_AREA_SERVICE)) {
					event.setProviderId(birthRegEvent.getProviderId());
					event.setLocationId(birthRegEvent.getLocationId());
					Map<String, String> details = new HashMap<String, String>();
					details.put("out_of_catchment_provider_id", event.getProviderId());
					event.setDetails(details);
				} else if (event.getEventType().contains(GROWTH_MONITORING_EVENT)
				        || event.getEventType().contains(VACCINATION_EVENT)) {
					
					String eventType = event.getEventType().contains(GROWTH_MONITORING_EVENT) ? GROWTH_MONITORING_EVENT
					        : event.getEventType().contains(VACCINATION_EVENT) ? VACCINATION_EVENT : null;
					if (eventType != null) {
						Event newEvent = new Event();
						newEvent.withBaseEntityId(event.getBaseEntityId()).withEventType(eventType)
						        .withEventDate(event.getEventDate()).withEntityType(event.getEntityType())
						        .withProviderId(birthRegEvent.getProviderId()).withLocationId(birthRegEvent.getLocationId())
						        .withFormSubmissionId(UUID.randomUUID().toString()).withDateCreated(event.getDateCreated());
						
						newEvent.setObs(event.getObs());
						addEvent(newEvent);
					}
				}
				//Legacy code only picked the first item so we break
				if (!isCardId) {
					break;
				}
				
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
		
		return event;
	}
	
	public synchronized Event addorUpdateEvent(Event event) {
		Event existingEvent = findById(event.getId());
		if (existingEvent != null) {
			event.setDateEdited(DateTime.now());
			event.setServerVersion(event.getServerVersion());
			event.setRevision(existingEvent.getRevision());
			allEvents.update(event);
			
		} else {
			event.setDateCreated(DateTime.now());
			allEvents.add(event);
			
		}
		
		return event;
	}
	
	public void updateEvent(Event updatedEvent) {
		// If update is on original entity
		if (updatedEvent.isNew()) {
			throw new IllegalArgumentException(
			        "Event to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}
		
		updatedEvent.setDateEdited(DateTime.now());
		
		allEvents.update(updatedEvent);
	}
	
	//TODO Review and add test cases as well
	public Event mergeEvent(Event updatedEvent) {
		try {
			Event original = find(updatedEvent);
			if (original == null) {
				throw new IllegalArgumentException("No event found with given list of identifiers. Consider adding new!");
			}
			
			original = (Event) Utils.getMergedJSON(original, updatedEvent, Arrays.asList(Event.class.getDeclaredFields()),
			    Event.class);
			for (Obs o : updatedEvent.getObs()) {
				// TODO handle parent
				if (original.getObs(null, o.getFieldCode()) == null) {
					original.addObs(o);
				} else {
					original.getObs(null, o.getFieldCode()).setComments(o.getComments());
					original.getObs(null, o.getFieldCode()).setEffectiveDatetime(o.getEffectiveDatetime());
					original.getObs(null, o.getFieldCode())
					        .setValue(o.getValues().size() < 2 ? o.getValue() : o.getValues());
				}
			}
			for (String k : updatedEvent.getIdentifiers().keySet()) {
				original.addIdentifier(k, updatedEvent.getIdentifier(k));
			}
			
			original.setDateEdited(DateTime.now());
			allEvents.update(original);
			return original;
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Event> findByServerVersion(long serverVersion) {
		return allEvents.findByServerVersion(serverVersion);
	}
	
	public List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		return allEvents.notInOpenMRSByServerVersion(serverVersion, calendar);
	}
	
	public List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar) {
		return allEvents.notInOpenMRSByServerVersionAndType(type, serverVersion, calendar);
	}
	
	public List<Event> getAll() {
		return allEvents.getAll();
	}
	
	public List<Event> findEvents(EventSearchBean eventSearchBean, String sortBy, String sortOrder, int limit) {
		return allEvents.findEvents(eventSearchBean, sortBy, sortOrder, limit);
	}
	
	public List<Event> findEvents(EventSearchBean eventSearchBean) {
		return allEvents.findEvents(eventSearchBean);
	}
	
	public List<Event> findEventsByConceptAndValue(String concept, String conceptValue) {
		return allEvents.findByConceptAndValue(concept, conceptValue);
		
	}
	
	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		return allEvents.findByBaseEntityAndType(baseEntityId, eventType);
		
	}
	
	private Event getUniqueEventFromEventList(List<Event> events) throws IllegalArgumentException {
		if (events.size() > 1) {
			throw new IllegalArgumentException();
		}
		if (events.size() == 0) {
			return null;
		}
		return events.get(0);
	}
	
	public List<Event> findByProviderAndEntityType(String provider) {
		return allEvents.findByProvider(provider);
	}

	/**
	 * This method searches for event ids filtered by eventType
	 * @param eventType used to filter the event ids
	 * @return a list of event ids
	 */
	public List<String> findAllIdsByEventType(String eventType) {
		return allEvents.findIdsByEventType(eventType);
	}
}
