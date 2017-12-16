
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

insert into local.users (id, email, name, password, secret)
    values (2c1f4296-1178-4fc5-81f7-bd511b0f1e44, 'test', 'test', '5fd924625f6ab16a19cc9807c7c506ae1813490e4ba675f843d5a10e0baacdb8', 'asd');


-- ###############################
-- ###############################
-- ###############################
create table local.subscriptions (
    id uuid,
    name text,
    css_selector text,
    url text,
    user_id uuid,
    primary key (user_id, name)
) with clustering order by (name asc);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (64fa5aa7-a56a-4e98-9447-07335c0107fa, 'Current Ethereum price', 'span.text-large2:nth-child(1)', 'https://coinmarketcap.com/currencies/ethereum/', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (cb078057-d212-4805-bafd-131e10303e95, 'Latest Typelevel blog post', 'div.post:nth-child(1) > div:nth-child(3) > p:nth-child(2)', 'https://typelevel.org/blog/', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (578ec339-c168-457e-9b59-d3c8b58cf94a, 'Latest Idris-lang version', '#recent-posts-2 > ul:nth-child(2) > li:nth-child(1) > a:nth-child(1)', 'https://www.idris-lang.org/download/', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (31fa52bd-ce4f-48f1-8867-adc5530108a3, 'Temp Las Palmas', '.local-temp', 'https://www.accuweather.com/en/es/las-palmas-de-gran-canaria/303032/weather-forecast/303032', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (72bf9b0c-6d52-44f8-9671-89ef6907480e, 'Play version', 'ul.tag-references:nth-child(2) > li:nth-child(1) > a:nth-child(1) > span:nth-child(2)', 'https://github.com/playframework/playframework/releases', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (7064adee-a98f-4d28-9929-3413004677e3, 'StackOverflow Scala tags', '.summarycount', 'https://stackoverflow.com/questions/tagged/scala', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (78958832-6271-43bf-8968-2256d6759a9a, 'Current Bitcoin price', 'span.text-large2:nth-child(1)', 'https://coinmarketcap.com/currencies/bitcoin/', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (5527c20b-2b5d-4190-aa03-eb1548029e85, 'Typelevel Cats version', 'div.release:nth-child(1) > div:nth-child(2) > div:nth-child(1) > h1:nth-child(1) > a:nth-child(1)', 'https://github.com/typelevel/cats/releases', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);

insert into local.subscriptions (id, name, css_selector, url, user_id)
    values (e4eb5685-f260-4f18-a239-61bb6d4151c2, 'Number StackOverflow Scala jobs', '.description', 'https://stackoverflow.com/jobs?sort=i&q=scala', 2c1f4296-1178-4fc5-81f7-bd511b0f1e44);


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

