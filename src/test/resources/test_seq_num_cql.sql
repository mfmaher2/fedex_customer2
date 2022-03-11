USE customer_test;

CREATE TABLE IF NOT EXISTS sequence_num_tbl (
    domain text,
    sequence_name text,
    current_num int,
    end_num int,
    start_num int,
    is_wrapped boolean,
    PRIMARY KEY ((domain, sequence_name)));

CREATE TABLE IF NOT EXISTS customer_test.id_available (
    domain text,
    identifier text,
    PRIMARY KEY (domain, identifier)
) WITH CLUSTERING ORDER BY(identifier ASC)
;

CREATE TABLE IF NOT EXISTS customer_test.id_assignment (
    domain text,
    identifier text,
    assigned_by text,
    PRIMARY KEY (domain, identifier)
) WITH CLUSTERING ORDER BY(identifier ASC)
;

CREATE TABLE IF NOT EXISTS customer_test.id_assignment_single (
    domain text,
    identifier text,
    assigned_by text,
    PRIMARY KEY (domain, identifier)
) WITH CLUSTERING ORDER BY(identifier ASC)
;