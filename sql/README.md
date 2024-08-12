# SQL Query Practice

This project is designed as a learning activity to learn SQL and RDBMS, by practicing and implementing various SQL commands.
This was implemented with the goal of learning and improving DDL, DQL, and DML commands. PgAdmin4 is used as the PSQL management tool in this project.

# SQL Queries

###### Table Setup (DDL)
```sql
 CREATE TABLE cd.members
(
    memid integer NOT NULL, 
    surname varchar(200) NOT NULL, 
    firstname varchar(200) NOT NULL, 
    address varchar(300) NOT NULL, 
    zipcode integer NOT NULL, 
    telephone varchar(20) NOT NULL, 
    recommendedby integer,
    joindate timestamp NOT NULL,
    CONSTRAINT members_pk PRIMARY KEY (memid),
    CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby) 
        REFERENCES cd.members(memid) ON DELETE SET NULL
);

CREATE TABLE cd.facilities
(
    facid integer NOT NULL,
    name varchar(100) NOT NULL,
    membercost numeric NOT NULL,
    guestcost numeric NOT NULL,
    initialoutlay numeric NOT NULL,
    monthlymaintenance numeric NOT NULL,
    CONSTRAINT facilities_pk PRIMARY KEY (facid)
);

CREATE TABLE cd.bookings
(
    bookid integer NOT NULL,
    facid integer NOT NULL,
    memid integer NOT NULL,
    starttime timestamp NOT NULL,
    slots integer NOT NULL,
    CONSTRAINT bookings_pk PRIMARY KEY (bookid),
    CONSTRAINT fk_bookings_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
    CONSTRAINT fk_bookings_memid FOREIGN KEY (memid) REFERENCES cd.members(memid)
);
```
To initialize the database with sample data run the `clubdata.sql` file.
<hr>

###### Question 1: Insert Values facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800
```sql
insert into cd.facilities values (9, 'spa', 20, 30, 100000, 800);
```

###### Question 2: Insert values Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800 but increment the latest facid by 
```sql
insert into cd.facilities
    (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
    select (select max(facid) from cd.facilities)+1, 'Spa', 20, 30, 100000, 800;
```

###### Question 3: Update initialoutlay with 10000
```sql
update cd.facilities
    set initialoutlay = 10000
    where facid = 1;
```

###### Question 4: Update the cost of 2nd tennis court to be 10% more than the first one
```sql
update cd.facilities faci
    set
        membercost = faci2.membercost * 1.1,
        guestcost = faci2.guestcost * 1.1
        from (select * from cd.facilities where facid = 0) faci2
    where faci.facid = 1;
```

###### Question 5: Delete all the bookings from the bookings table
```sql
truncate cd.bookings;
```

###### Question 6: Remove member 37
```sql
delete from cd.members where memid = 37;
```

###### Question 7: List of facilities that charge a fee to members which is less than 1/50th of the monthly maintenance cost
```sql
select facid, name, membercost, monthlymaintenance from cd.facilities
where membercost > 0 and (membercost < monthlymaintenance / 50);
```

###### Question 8: List of facilities with the word "Tennis" in their name
```sql
select * from cd.facilities where name like '%Tennis%';
```

###### Question 9: Details of facilities with ID 1 and 5 without using OR
```sql
select * from cd.facilities where facid in (1,5);
```

###### Question 10: List of members who joined after the start of September 2012
```sql
select memid, surname, firstname, joindate from cd.members where joindate >= '2012-09-01';
```

###### Question 11: Combined list of all surnames and all facility names
```sql
select surname from cd.members union select name from cd.facilities;
```

###### Question 12: List of start times for bookings by members named "David Farrell"
```sql
select books.starttime from cd.bookings books
    inner join cd.members membs on membs.memid = books.memid
    where membs.firstname = 'David' and membs.surname = 'Farrell';
```

###### Question 13: List of start times for tennis courts bookings for the date 2012-09-21, order by time
```sql
select books.starttime as start, faci.name as name
    from
        cd.facilities faci
            inner join cd.bookings books
                       on faci.facid = books.facid
    where
        faci.name like '%Tennis Court%' and
        books.starttime >= '2012-09-21' and
        books.starttime < '2012-09-22'
order by books.starttime;
```

###### Question 14: List of all members including the name of the person who recommended them (optional), ordered by surname and firstname
```sql
select membs.firstname as memfname, membs.surname as memsname, recom.firstname as recfname, recom.surname as recsname
    from cd.members membs
             left outer join cd.members recom
                             on recom.memid = membs.recommendedby
order by memsname, memfname;
```

###### Question 15: List of all members who have recommended other members without duplicates, ordered by surname and firstname
```sql
select distinct recom.firstname as firstname, recom.surname as surname
    from cd.members membs
             inner join cd.members recom
                        on recom.memid = membs.recommendedby
order by surname, firstname;
```

###### Question 16: List of all members including the name of the person who recommended them without using joins, no duplicates and ordered by firstname combined with surname as one column
```sql
select distinct membs.firstname || ' ' || membs.surname as member,
    (select recom.firstname || ' ' || recom.surname as recommender
        from cd.members recom where recom.memid = membs.recommendedby
    )
    from cd.members membs
order by member;
```

###### Question 17: Count the number of recommendations each member has made, order by member ID
```sql
select recommendedby, count(*)
    from cd.members
    where recommendedby is not null
    group by recommendedby
order by recommendedby;
```

###### Question 18: List of total number of slots booked per facility, sorted by facility id
```sql
select facid, sum(slots) as "Total Slots"
    from cd.bookings
    group by facid
order by facid;
```

###### Question 19: List of total number of slots booked per facility in the month of September 2012, sorted by number of slots
```sql
select facid, sum(slots) as "Total Slots"
    from cd.bookings where starttime >= '2012-09-01' and starttime < '2012-10-01'
    group by facid
order by sum(slots);
```

###### Question 20: List total number of slots booked per facility per month in the year of 2012, sorted by id and month
```sql
select facid, extract(month from starttime) as month, sum(slots) as "Total Slots"
    from cd.bookings where starttime >= '2012-01-01' and starttime < '2013-01-01'
    group by facid, month
order by facid, month;
```

###### Question 21: Total number of members who have made at least one booking
```sql
select count (distinct memid) from cd.bookings;
```

###### Question 22: List of each member and their first booking after September 1st 2012, sorted by member ID
```sql
select memb.surname, memb.firstname, memb.memid, min(book.starttime) as starttime
    from cd.bookings book
             inner join cd.members memb
                        on memb.memid = book.memid
    where starttime >= '2012-09-01'
    group by memb.surname, memb.firstname, memb.memid
order by memb.memid;
```

###### Question 23: List of member names with each row containing the total member count. order by join date
```sql
select count(*) over(), firstname, surname
    from cd.members
order by joindate;
```

###### Question 24: A monotonically increasing numbered list of members ordered by their date of joining
```sql
select row_number() over(order by joindate), firstname, surname
    from cd.members
order by joindate;
```

###### Question 25: Show the facility id that has the highest number of slots booked
```sql
select facid, total from (
     select facid, total, rank() over (order by total desc) rank from (
          select facid, sum(slots) total
          from cd.bookings
          group by facid
    ) as sumslots
) as ranked
where rank = 1;
```

###### Question 26: Show the names of all members, formatted as "Surname, Firstname"
```sql
select surname || ', ' || firstname as name from cd.members;
```

###### Question 27: Show all the telephone numbers that have parentheses in them
```sql
select memid, telephone from cd.members where telephone similar to '%[()]%';
```

###### Question 28: Count how many members surname starts with each letter of the alphabet, sort by letter
```sql
select substr (cd.members.surname,1,1) as letter, count(*) as count
    from cd.members
    group by letter
order by letter;
```
