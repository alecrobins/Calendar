create table if not exists user (
	id integer primary key AUTO_INCREMENT,
	username varchar(50) not null,
	password varchar(50) not null,
	first_name varchar(200),
	last_name varchar (200),
	email varchar(200) not null,
	isAdmin boolean not null
);

create table if not exists location (
	id integer primary key AUTO_INCREMENT,
	name varchar(200) not null unique,
	capacity integer, 
	isGroupFacility boolean
);

create table if not exists event (
	id integer primary key AUTO_INCREMENT,
	startTime timestamp,
	endTime timestamp,
	eventTitle varchar(200), 
	eventDescription varchar(200),
	eventReminderStart timestamp,
	eventReminderEnd timestamp,
	/* Frequency - 0 is onetime, 1 - daily, 2 - weekly, 3 - monthly */
	frequency tinyint not null,
	locationID int not null,
	isGroup boolean not null,
	isPublic boolean not null,

	foreign key (locationID)
	references location(id)
		on delete cascade
		on update cascade
);

create table if not exists groupEvent (
	eventID integer not null,
	userInitiator int not null,
	confirmed boolean,
	primary key (eventID),

	foreign key (eventID)
		references event(id)
		on delete cascade
		on update cascade,

	foreign key (userInitiator)
		references user(id)
		on delete cascade
);

create table if not exists groupUser (
	eventID integer not null,
	userID integer not null,
	approved boolean,
	primary key (eventID, userID),

	foreign key (eventID)
		references event(id)
		on delete cascade
		on update cascade,

	foreign key (userID)
		references user(id)
		on delete cascade

);

create table if not exists userEvent (
	userID integer not null,
	eventID integer not null,
	primary key (eventID, userID),

	foreign key (eventID)
		references event(id)
		on delete cascade
		on update cascade,

	foreign key (userID)
		references user(id)
		on delete cascade

);

create table if not exists groupUserTimeSlots(
	initiatorID integer not null,
	userID integer not null,
	eventID integer not null,
	startTime timestamp not null,
	endTime timestamp not null,

	primary key (initiatorID, userID, eventID, startTime),

	foreign key (initiatorID)
		references user(id)
		on delete cascade,
	
	foreign key (userID)
		references user(id)
		on delete cascade,

	foreign key (eventID)
		references event(id)
		on delete cascade
		on update cascade
);

create table if not exists groupUserTimeSlotsSelected(
	initiatorID integer not null,
	userID integer not null,
	eventID integer not null,
	startTime timestamp,
	endTime timestamp,

	primary key (initiatorID, userID, eventID, startTime),

	foreign key (initiatorID)
		references user(id)
		on delete cascade,

	foreign key (userID)
		references user(id)
		on delete cascade,

	foreign key (eventID)
		references event(id)
		on delete cascade
);
