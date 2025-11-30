CREATE TABLE folarink.Guests (
    guest_id INT PRIMARY KEY,
    guest_name VARCHAR(50)
);

CREATE TABLE folarink.Hosts (
    host_id INT PRIMARY KEY,
    join_date DATE NOT NULL,
    host_identity_verified BIT NOT NULL,
    host_name VARCHAR(50)
);

CREATE TABLE folarink.Neighbourhoods (
    neighbourhood_id INT PRIMARY KEY,
    neighbourhood_name VARCHAR(50) NOT NULL,
);


CREATE TABLE folarink.Listings (
    listing_id INT PRIMARY KEY,
    host_id INT,
    name VARCHAR(100) NOT NULL,
    price FLOAT NOT NULL,
    property_type VARCHAR(50) NOT NULL,
    review_scores_value FLOAT CHECK (review_scores_value >= 0 AND review_scores_value <= 5),
    neighbourhood_id INT,
    FOREIGN KEY (neighbourhood_id) REFERENCES folarink.Neighbourhoods(neighbourhood_id),
    FOREIGN KEY (host_id) REFERENCES folarink.Hosts(host_id)
);

CREATE TABLE folarink.Reviews (
    review_id INT PRIMARY KEY,
    comments TEXT,
    guest_id INT,
    listing_id INT,
    FOREIGN KEY (guest_id) REFERENCES folarink.Guests(guest_id),
    FOREIGN KEY (listing_id) REFERENCES folarink.Hosts(listing_id)
);

CREATE TABLE folarink.Criminals(
    criminal_id INT PRIMARY KEY,
    criminal_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    age_group VARCHAR(20) NOT NULL, 
    number_of_prior_convictions INT NOT NULL
);

CREATE TABLE folarink.Crimes(
    crime_id INT PRIMARY KEY,
    crime_type VARCHAR(100) NOT NULL,
    criminal_id INT,
    neighbourhood_id INT,
    FOREIGN KEY (neighbourhood_id) REFERENCES folarink.Neighbourhoods(neighbourhood_id),
    FOREIGN KEY (criminal_id) REFERENCES folarink.Criminals(criminal_id)
);