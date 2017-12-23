
create keyspace local with replication = {'class':'SimpleStrategy', 'replication_factor':1};

-- ######################
-- ######################
-- ######################
create table local.users (
    id uuid primary key,
    email text,
    name text,
    password text,
    secret text,
    token_expiration timestamp,
    user_token text
);


-- ###############################
-- ###############################
-- ###############################
create table local.subscriptions (
    id uuid,
    name text,
    css_selector text,
    url text,
    user_id uuid,
    use_html_extractor boolean,
    primary key (user_id, name)
) with clustering order by (name asc);


-- ##########################################
-- ##########################################
-- ##########################################
create table local.subscription_feed_values (
    id uuid,
    subscription_id uuid,
    last_updated timestamp,
    value text,
    primary key (subscription_id, last_updated)
) with clustering order by (last_updated desc);

