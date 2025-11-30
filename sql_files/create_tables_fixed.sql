-- create_tables_fixed.sql
-- Creates tables with columns/types adjusted to match the provided INSERT files.
-- Run this on your SQL Server (e.g. in SSMS or with sqlcmd) before running the Populate loader.

-- Drop child tables first (if they exist)
IF OBJECT_ID('guests_book_listings','U') IS NOT NULL DROP TABLE guests_book_listings;
IF OBJECT_ID('guest_visit_attractions','U') IS NOT NULL DROP TABLE guest_visit_attractions;
IF OBJECT_ID('listings_have_amenities','U') IS NOT NULL DROP TABLE listings_have_amenities;
IF OBJECT_ID('reviews','U') IS NOT NULL DROP TABLE reviews;
IF OBJECT_ID('listings','U') IS NOT NULL DROP TABLE listings;
IF OBJECT_ID('guests','U') IS NOT NULL DROP TABLE guests;
IF OBJECT_ID('hosts','U') IS NOT NULL DROP TABLE hosts;
IF OBJECT_ID('amenities','U') IS NOT NULL DROP TABLE amenities;
IF OBJECT_ID('attractions','U') IS NOT NULL DROP TABLE attractions;
IF OBJECT_ID('police_stations','U') IS NOT NULL DROP TABLE police_stations;
IF OBJECT_ID('neighbourhoods','U') IS NOT NULL DROP TABLE neighbourhoods;
IF OBJECT_ID('crimes','U') IS NOT NULL DROP TABLE crimes;
IF OBJECT_ID('criminals','U') IS NOT NULL DROP TABLE criminals;

-- Parent tables
CREATE TABLE neighbourhoods (
    neighbourhood_id BIGINT PRIMARY KEY,
    neighbourhood_name VARCHAR(255) NOT NULL
);

CREATE TABLE amenities (
    amenity_id BIGINT PRIMARY KEY,
    amenity_name VARCHAR(255) NOT NULL
);

CREATE TABLE hosts (
    host_id BIGINT PRIMARY KEY,
    host_name VARCHAR(255) NULL,
    host_since DATE NULL,
    host_identity_verified CHAR(1) NULL
);

CREATE TABLE guests (
    guest_id BIGINT PRIMARY KEY,
    guest_name VARCHAR(255) NULL
);

CREATE TABLE listings (
    listing_id BIGINT PRIMARY KEY,
    name VARCHAR(500) NULL,
    price DECIMAL(12,2) NULL,
    property_type VARCHAR(255) NULL,
    review_scores_value DECIMAL(5,2) NULL,
    host_id BIGINT NULL,
    neighbourhood_name VARCHAR(255) NULL,
    neighbourhood_id BIGINT NULL,
    CONSTRAINT FK_listings_hosts FOREIGN KEY (host_id) REFERENCES hosts(host_id),
    CONSTRAINT FK_listings_neighbourhoods FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

CREATE TABLE attractions (
    attraction_id BIGINT PRIMARY KEY,
    attraction_name VARCHAR(255) NULL,
    neighbourhood_id BIGINT NULL,
    CONSTRAINT FK_attractions_neighbourhoods FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

CREATE TABLE police_stations (
    police_station_id BIGINT PRIMARY KEY,
    police_station_name VARCHAR(255) NULL,
    neighbourhood_id BIGINT NULL,
    CONSTRAINT FK_police_neighbourhoods FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

-- Supporting / relationship tables
CREATE TABLE reviews (
    listing_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    comments NVARCHAR(MAX) NULL,
    PRIMARY KEY (listing_id, review_id),
    CONSTRAINT FK_reviews_listings FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE,
    CONSTRAINT FK_reviews_guests FOREIGN KEY (guest_id) REFERENCES guests(guest_id)
);

CREATE TABLE listings_have_amenities (
    listing_id BIGINT NOT NULL,
    amenity_id BIGINT NOT NULL,
    PRIMARY KEY (listing_id, amenity_id),
    CONSTRAINT FK_lha_listings FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE,
    CONSTRAINT FK_lha_amenities FOREIGN KEY (amenity_id) REFERENCES amenities(amenity_id) ON DELETE CASCADE
);

CREATE TABLE guest_visit_attractions (
    guest_id BIGINT NOT NULL,
    attraction_id BIGINT NOT NULL,
    PRIMARY KEY (guest_id, attraction_id),
    CONSTRAINT FK_gva_guests FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
    CONSTRAINT FK_gva_attractions FOREIGN KEY (attraction_id) REFERENCES attractions(attraction_id) ON DELETE CASCADE
);

CREATE TABLE guests_book_listings (
    guest_id BIGINT NOT NULL,
    listing_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    PRIMARY KEY (guest_id, listing_id, booking_date),
    CONSTRAINT FK_gbl_guests FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
    CONSTRAINT FK_gbl_listings FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE
);

-- Crime and criminal tables
CREATE TABLE criminals (
    criminal_id BIGINT PRIMARY KEY,
    criminal_name VARCHAR(255) NULL,
    gender VARCHAR(20) NULL,
    number_of_prior_convictions INT NULL,
    age_group VARCHAR(50) NULL
);

CREATE TABLE crimes (
    crime_id BIGINT PRIMARY KEY,
    crime_type VARCHAR(255) NULL,
    criminal_id BIGINT NULL,
    neighbourhood_id BIGINT NULL,
    CONSTRAINT FK_crimes_criminals FOREIGN KEY (criminal_id) REFERENCES criminals(criminal_id),
    CONSTRAINT FK_crimes_neighbourhoods FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

GO

-- Notes:
-- 1) `host_identity_verified` kept as CHAR(1) because the provided inserts use 't'/'f'.
--    If you prefer BIT, update the INSERT files to use 1/0 instead.
-- 2) Some inserts include very large numeric IDs; columns use BIGINT to avoid overflow.
-- 3) If you already have a custom schema (non-dbo) and need tables created under that schema,
--    modify the object names accordingly (e.g. schema.table).
