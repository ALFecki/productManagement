DROP SCHEMA ikassa CASCADE;
-- DROP SCHEMA public CASCADE;

-- CREATE SCHEMA public
--     AUTHORIZATION root;

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
    "alias" character varying NOT NULL UNIQUE,
    "name" character varying NOT NULL,
    "comment" character varying,
    "price" double precision,
    "tax" double precision,
    "currency" character varying,
    "units" character varying,
    "round_total" boolean,
    "dual_docs" boolean,
    PRIMARY KEY("product_id")
);
ALTER TABLE IF EXISTS ikassa.product
            OWNER TO root;


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
    "alias" character varying NOT NULL UNIQUE,
    "name" character varying NOT NULL,
    "price" double precision,
    "extra_vars" jsonb,
    "legal_name" character varying NOT NULL,
    "version" character varying NOT NULL,
    PRIMARY KEY (solution_id)
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

CREATE TABLE ikassa."solution_instruction" (
    "instruction_id" bigserial,
    "solution_id" bigint,
    "accompanying_doc_id" bigint,
    PRIMARY KEY ("instruction_id"),
    FOREIGN KEY ("solution_id") REFERENCES ikassa."solution"("solution_id"),
    FOREIGN KEY ("accompanying_doc_id") REFERENCES ikassa."accompanying_doc"("accompanying_doc_id")
);
ALTER TABLE IF EXISTS ikassa."solution_instruction"
    OWNER TO root;


CREATE TABLE ikassa."partner_form" (
    "partner_form_id" bigserial NOT NULL,
    "unp" INT NOT NULL,
    "name" character varying NOT NULL,
    "logo" character varying NOT NULL,
    "name_remap" jsonb,
    "emails" character varying[],
    "allow_manual" boolean,
    "description" character varying,
    "available_periods" INT[],
    "slug" character varying,
    PRIMARY KEY (partner_form_id)
);

ALTER TABLE IF EXISTS ikassa."partner_form"
    OWNER TO root;

CREATE TABLE ikassa."form_solution" (
    "partner_form_id" bigint,
    "solution_id" bigint,
    FOREIGN KEY ("partner_form_id") REFERENCES ikassa."partner_form"("partner_form_id"),
    FOREIGN KEY ("solution_id") REFERENCES ikassa."solution"("solution_id")
);
ALTER TABLE IF EXISTS ikassa."form_solution"
    OWNER TO root;

CREATE TABLE ikassa."email_mode" (
    "email_mode_id" bigserial NOT NULL,
    "name" character varying,
    "send_to_client" boolean,
    "send_to_partner" boolean,
    PRIMARY KEY ("email_mode_id")
);
ALTER TABLE IF EXISTS ikassa."email_mode"
    OWNER TO root;

CREATE TABLE ikassa."partner_form_email_mode" (
    "partner_form_id" bigint,
    "email_mode_id" bigint,
    FOREIGN KEY ("partner_form_id") REFERENCES ikassa."partner_form"("partner_form_id"),
    FOREIGN KEY ("email_mode_id") REFERENCES ikassa."email_mode"("email_mode_id")
);
ALTER TABLE IF EXISTS ikassa."partner_form_email_mode"
    OWNER TO root;


CREATE TABLE ikassa."document" (
    "document_id" bigserial NOT NULL,
    "alias" character varying,
    "name" character varying,
    "path" character varying,
    PRIMARY KEY ("document_id")
);
ALTER TABLE IF EXISTS ikassa."document"
    OWNER TO root;


CREATE TABLE ikassa."util" (
    "util_id" bigserial NOT NULL,
    "name" character varying NOT NULL UNIQUE,
    "data" text,
    PRIMARY KEY (util_id)
);
ALTER TABLE IF EXISTS ikassa."util"
    OWNER TO root;