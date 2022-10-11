DROP SCHEMA ikassa CASCADE;

CREATE SCHEMA ikassa
    AUTHORIZATION root; -- need to be refactored (testing example)


CREATE TABLE ikassa.accompanying_doc (
    "accompanying_doc_id" bigserial NOT NULL,
    "path" character varying NOT NULL,
    "name" character varying,
    "field" jsonb,
    "raw" boolean,
    PRIMARY KEY ("accompanying_doc_id")
);
ALTER TABLE IF EXISTS ikassa.accompanying_doc
    OWNER TO root;


CREATE TABLE ikassa."product" (
    "product_id" bigserial NOT NULL,
    "alias" character varying,
    "name" character varying NOT NULL,
    "comment" character varying,
    "price" double precision,
    "tax" double precision,
    "currency" character varying,
    "units" int,
    "round_total" boolean,
    "dual_docs" boolean,
    PRIMARY KEY("product_id")
);
ALTER TABLE IF EXISTS ikassa.product
            OWNER TO root;


--TODO("сделать, чтобы каждый продукт мог иметь только уникальные документы")
CREATE TABLE ikassa."products_accompanying_docs" (
    "pr_acc_link_id" bigserial ,
    "product_id" bigint,
    "accompanying_doc_id" bigint,
    PRIMARY KEY ("pr_acc_link_id"),
    FOREIGN KEY ("product_id") REFERENCES ikassa."product"("product_id"),
    FOREIGN KEY ("accompanying_doc_id") REFERENCES ikassa."accompanying_doc"("accompanying_doc_id")
);
ALTER TABLE IF EXISTS ikassa."products_accompanying_docs"
    OWNER TO root;

CREATE TABLE ikassa."solution" (
    "solution_id" bigserial NOT NULL,
    "alias" character varying NOT NULL,
    "name" character varying NOT NULL,
    "price" double precision,
    "extra_vars" jsonb,
    "legal_name" character varying NOT NULL,
    "version" character varying NOT NULL,
    "forced_instruction_link" bigserial,
    PRIMARY KEY (solution_id)--,
--     FOREIGN KEY ("forced_instruction_link") REFERENCES ikassa.accompanying_doc("accompanying_doc_id")
);

ALTER TABLE IF EXISTS ikassa."solution"
    OWNER TO root;

CREATE TABLE ikassa."solutions_products" (
    "sol_prod_link_id" bigserial ,
    "solution_id" bigint,
    "product_id" bigint,
    PRIMARY KEY ("sol_prod_link_id"),
    FOREIGN KEY ("solution_id") REFERENCES ikassa."solution"("solution_id"),
    FOREIGN KEY ("product_id") REFERENCES ikassa."product"("product_id")
);
ALTER TABLE IF EXISTS ikassa."solutions_products"
    OWNER TO root;

CREATE TABLE ikassa."solutions_related_products" (
    "sol_related_prod_link_id" bigserial,
    "solution_id" bigint,
    "product_id" bigint,
    PRIMARY KEY ("sol_related_prod_link_id"),
    FOREIGN KEY ("solution_id") REFERENCES ikassa."solution"("solution_id"),
    FOREIGN KEY ("product_id") REFERENCES ikassa."product"("product_id")
);
ALTER TABLE IF EXISTS ikassa."solutions_related_products"
    OWNER TO root;

CREATE TABLE ikassa."solutions_accompanying_docs" (
    "sol_acc_doc_link_id" bigserial,
    "solution_id" bigint,
    "accompanying_doc_id" bigint,
    PRIMARY KEY ("sol_acc_doc_link_id"),
    FOREIGN KEY ("solution_id") REFERENCES ikassa."solution"("solution_id"),
    FOREIGN KEY ("accompanying_doc_id") REFERENCES ikassa."accompanying_doc"("accompanying_doc_id")
);
ALTER TABLE IF EXISTS ikassa."solutions_accompanying_docs"
    OWNER TO root;

CREATE TABLE ikassa."solutions_equipment" (
    "solution_equipment_id" bigserial,
    "solution_id" bigint,
    "product_id" bigint,
    PRIMARY KEY ("solution_equipment_id"),
    FOREIGN KEY ("solution_id") REFERENCES ikassa."solution"("solution_id"),
    FOREIGN KEY ("product_id") REFERENCES ikassa."product"("product_id")
);
ALTER TABLE IF EXISTS ikassa."solutions_equipment"
    OWNER TO root;