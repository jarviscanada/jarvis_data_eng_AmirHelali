-- Insert Values facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800
insert into cd.facilities
values
    (9, 'spa', 20, 30, 100000, 800);

-- Insert values Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800 but increment the latest facid by 1
insert into cd.facilities
    (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
    select (select max(facid) from cd.facilities)+1, 'Spa', 20, 30, 100000, 800;

-- Update initialoutlay
update cd.facilities
    set initialoutlay = 10000
    where facid = 1;

-- Update the cost of 2nd tennis court to be 10% more than the first one
update cd.facilities faci
    set
        membercost = faci2.membercost * 1.1,
        guestcost = faci2.guestcost * 1.1
        from (select * from cd.facilities where facid = 0) faci2
    where faci.facid = 1;

-- delete all the bookings from the bookings table
truncate cd.bookings;

-- remove member 37
delete from cd.members where memid = 37;

-- List of facilities that charge a fee to members which is less than 1/50th of the monthly maintenace cost
select facid, name, membercost, monthlymaintenance from cd.facilities
where membercost > 0 and (membercost < monthlymaintenance / 50);

-- List of facilities with the word "Tennis" in their name
select * from cd.facilities where name like '%Tennis%';

-- Details of facilities with ID 1 and 5 without using OR
select * from cd.facilities where facid in (1,5);

-- List of members who joined after the start of September 2012
select memid, surname, firstname, joindate from cd.members where joindate >= '2012-09-01';

-- Combined list of all surnames and all facility names
select surname from cd.members union select name from cd.facilities;

-- List of start times for bookings by members named "David Farrell"
select books.starttime from cd.bookings books
    inner join cd.members membs on membs.memid = books.memid
    where membs.firstname = 'David' and membs.surname = 'Farrell';

-- List of start times for tennis courts bookings for the date 2012-09-21 order by time
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

-- List of all members including the name of the person who recommended them (optional), ordered by surname and firstname
select membs.firstname as memfname, membs.surname as memsname, recom.firstname as recfname, recom.surname as recsname
    from cd.members membs
             left outer join cd.members recom
                             on recom.memid = membs.recommendedby
order by memsname, memfname;

-- List of all members who have recommended other members without duplicates, ordered by surname and firstname
select distinct recom.firstname as firstname, recom.surname as surname
    from cd.members membs
             inner join cd.members recom
                        on recom.memid = membs.recommendedby
order by surname, firstname;

-- List of all members including the name of the person who recommended them without using joins, no duplicates and ordered by firstname combined with surname as one column
select distinct membs.firstname || ' ' || membs.surname as member,
    (select recom.firstname || ' ' || recom.surname as recommender
        from cd.members recom where recom.memid = membs.recommendedby
    )
    from cd.members membs
order by member;

-- Count the number of recommendations each member has made, order by member ID
select recommendedby, count(*)
    from cd.members
    where recommendedby is not null
    group by recommendedby
order by recommendedby;

-- List of total number of slots booked per facility, sorted by facility id
select facid, sum(slots) as "Total Slots"
    from cd.bookings
    group by facid
order by facid;

-- List of total number of slots booked per facility in the month of September 2012, sorted by number of slots
select facid, sum(slots) as "Total Slots"
    from cd.bookings where starttime >= '2012-09-01' and starttime < '2012-10-01'
    group by facid
order by sum(slots);

-- List total number of slots booked per facility per month in the year of 2012, sorted by id and month
select facid, extract(month from starttime) as month, sum(slots) as "Total Slots"
    from cd.bookings where starttime >= '2012-01-01' and starttime < '2013-01-01'
    group by facid, month
order by facid, month;

-- Total number of members who have made at least one booking
select count (distinct memid) from cd.bookings;

-- List of each member and their first booking after September 1st 2012, sorted by member ID
select memb.surname, memb.firstname, memb.memid, min(book.starttime) as starttime
    from cd.bookings book
             inner join cd.members memb
                        on memb.memid = book.memid
    where starttime >= '2012-09-01'
    group by memb.surname, memb.firstname, memb.memid
order by memb.memid;

-- List of member names with each row containing the total member count. order by join date
select count(*) over(), firstname, surname
    from cd.members
order by joindate;

-- A monotonically increasing numbered list of members ordered by their date of joining.
select row_number() over(order by joindate), firstname, surname
    from cd.members
order by joindate;

-- Show the facility id that has the highest number of slots booked
select facid, total from (
     select facid, total, rank() over (order by total desc) rank from (
          select facid, sum(slots) total
          from cd.bookings
          group by facid
    ) as sumslots
) as ranked
where rank = 1;

-- Show the names of all members, formatted as "Surname, Firstname"
select surname || ', ' || firstname as name from cd.members;

-- Show all the telephone numbers that have parentheses in them
select memid, telephone from cd.members where telephone similar to '%[()]%';

-- Count how many members surname starts with each letter of the alphabet, sort by letter.
select substr (cd.members.surname,1,1) as letter, count(*) as count
    from cd.members
    group by letter
order by letter;