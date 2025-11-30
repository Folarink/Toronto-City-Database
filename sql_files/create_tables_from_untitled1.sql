-- First create the neighbourhoods table (parent table)
CREATE TABLE neighbourhoods (
    neighbourhood_id INT PRIMARY KEY,
    neighbourhood_name VARCHAR(255) NOT NULL
);

-- Then create the base tables
CREATE TABLE amenities (
    amenity_id INT PRIMARY KEY,
    amenity_name VARCHAR(255) NOT NULL
);

CREATE TABLE attractions (
    attraction_id INT PRIMARY KEY,
    attraction_name VARCHAR(255) NOT NULL,
    neighbourhood_id INT NOT NULL,
    FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

CREATE TABLE police_stations (
    police_station_id INT PRIMARY KEY,
    police_station_name VARCHAR(255) NOT NULL,
    neighbourhood_id INT NOT NULL,
    FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

-- Create the listings table (parent of reviews weak entity)
CREATE TABLE listings (
    listing_id INT PRIMARY KEY,
    neighbourhood_id INT, -- Optional: if listings also belong to neighbourhoods
    FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods(neighbourhood_id)
);

-- Create the guests table
CREATE TABLE guests (
    guest_id INT PRIMARY KEY
);

-- Now create the relationship tables and weak entity

-- Weak entity: reviews (dependent on listings)
CREATE TABLE reviews (
    listing_id INT NOT NULL,
    review_id INT NOT NULL,
    guest_id INT NOT NULL,
    comments TEXT,
    PRIMARY KEY (listing_id, review_id),
    FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id)
);

-- Many-to-many relationship: listings_have_amenities
CREATE TABLE listings_have_amenities (
    listing_id INT NOT NULL,
    amenity_id INT NOT NULL,
    PRIMARY KEY (listing_id, amenity_id),
    FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE,
    FOREIGN KEY (amenity_id) REFERENCES amenities(amenity_id) ON DELETE CASCADE
);

-- Many-to-many relationship: guest_visit_attractions
CREATE TABLE guest_visit_attractions (
    guest_id INT NOT NULL,
    attraction_id INT NOT NULL,
    PRIMARY KEY (guest_id, attraction_id),
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attractions(attraction_id) ON DELETE CASCADE
);

-- Many-to-many relationship: guests_book_listings
CREATE TABLE guests_book_listings (
    guest_id INT NOT NULL,
    listing_id INT NOT NULL,
    booking_date DATE NOT NULL,
    PRIMARY KEY (guest_id, listing_id, booking_date),
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE
);
