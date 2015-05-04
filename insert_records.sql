/* insert dummy users */
INSERT INTO user (id,username,password,first_name,last_name,email,isAdmin) 
values (null, 'abcd', '1234', 'abc-first', 'xyz-second', 'abc@email.com', 'true');

/* insert dummy locations */
insert into location (id,name,isGroupFacility)
values (null, 'location1', 'false');

insert into location (id,name,isGroupFacility)
values (null, 'location2', 'false');

insert into location (id,name,isGroupFacility)
values (null, 'location3', 'true');

insert into location (id,name,isGroupFacility)
values (null, 'location4', 'true');

insert into location (id,name,isGroupFacility)
values (null, 'location5', 'false');

/* insert dummy events */
insert into event (id, startTime, endTime, eventTitle, eventDescription, eventReminderStart, eventReminderEnd, frequency, locationID, isGroup, isPublic)
values (null, 1600, 3600, 'first title', 'first description', 500, 1400, 0, 1, 'false', 'true');
