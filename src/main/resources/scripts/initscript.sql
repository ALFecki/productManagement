DROP SCHEMA ikassa CASCADE;

CREATE SCHEMA ikassa
    AUTHORIZATION root; -- need to be refactored (testing example)

CREATE TABLE ikassa."Product" (
    "product_id" bigserial NOT NULL,
    "alias" character varying,
    "name" character varying NOT NULL,
    "comment" character varying,
    "price" double precision,
    "soft_relations" integer[],
    "hard_relations" integer[],
    "tax" double precision,
    "currency" character varying,
    "units" int,
    "round_total" boolean,
    "dual_docs" boolean,
    PRIMARY KEY("product_id")
);
ALTER TABLE IF EXISTS ikassa."Product"
            OWNER TO root;

CREATE TABLE ikassa."AccompanyingDoc" (
    "acc_doc_id" serial NOT NULL,
    "path" character varying NOT NULL,
    "name" character varying,
    "raw" boolean,
--     "product_id" serial NOT NULL,
    PRIMARY KEY ("acc_doc_id")
);
ALTER TABLE IF EXISTS ikassa."AccompanyingDoc"
    OWNER TO root;


--TODO("сделать, чтобы каждый продукт мог иметь только уникальные документы")
CREATE TABLE ikassa."ProductAccDocLink" (
    "pr_acc_link_id" bigserial NOT NULL,
    "product_link" bigserial NOT NULL,
    "accompanying_link" serial NOT NULL,
    PRIMARY KEY ("pr_acc_link_id"),
    FOREIGN KEY ("product_link") REFERENCES ikassa."Product"("product_id"),
    FOREIGN KEY ("accompanying_link") REFERENCES ikassa."AccompanyingDoc"("acc_doc_id")
);

ALTER TABLE IF EXISTS ikassa."ProductAccDocLink"
    OWNER TO root;

CREATE TABLE ikassa."Solution" (
    "solution_id" bigserial NOT NULL,
    "alias" character varying,
    "price" double precision
);

ALTER TABLE IF EXISTS ikassa."Solution"
    OWNER TO root;

