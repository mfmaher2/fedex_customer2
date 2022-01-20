USE customer;

CREATE TABLE IF NOT EXISTS sequence_num_tbl (
    domain text,
    sequence_name text,
    current_num int,
    end_num int,
    start_num int,
    is_wrapped boolean,
    PRIMARY KEY ((domain, sequence_name)));
