package management.services

import jakarta.inject.Singleton
import management.data.docs.Document
import management.data.docs.RenderedDocuments
import management.data.products.Product
import management.data.products.Solution
import management.data.repositories.DocumentRepository
import management.forms.DocumentDto
import management.utils.FilePath.PATH_TO_FM
import management.utils.FilePath.PATH_TO_PAYMENT_LICENSE
import management.utils.FilePath.PATH_TO_PAYMENT_REGISTRATION
import management.utils.FilePath.PATH_TO_PAYMENT_SERVICE
import management.utils.FilePath.PATH_TO_PAYMENT_SKKO
import management.utils.FilePath.PATH_TO_SMART

@Singleton
class FillDocumentService (private val documentRepository: DocumentRepository) {

    val payments = mapOf(
        PATH_TO_SMART to listOf("100", "0.2"),
        PATH_TO_PAYMENT_REGISTRATION to listOf("35", "0"),
        PATH_TO_PAYMENT_LICENSE to listOf("15", "0"),
        PATH_TO_PAYMENT_SKKO to listOf("2.5", "0.2"),
        PATH_TO_PAYMENT_SERVICE to listOf("25", "0.2"),
        PATH_TO_FM to listOf("290", "0.2")
    )


    fun fillProductsDocuments(products : List<Product>, eqTotal : Short, fullData : DocumentDto? = null, solution: Solution? = null) : List<RenderedDocuments> {

        throw NotImplementedError()
    }


    public fun exportDefaultDocs() : List<Document> {
        return documentRepository.saveAll(
            listOf(
                Document(
                    name = "01-1 Заявление о присоединении к публичному договору СККО",
                    path = "skko/contract.docx",
                    alias = "skko_contract"
                ),
                Document(
                    name = "01-2 (если договор на СКНО заключен) Дополнительное соглашение",
                    path = "skko/contract_application.docx",
                    alias = "skko_contract_application"
                ),
                Document(
                    name = "02 Заявка на регистрацию и подключение ПК IKASSA",
                    path = "skko/connection_application.docx",
                    alias = "skko_connection_application"
                ),
                Document(
                    name = "03 Реквизиты для оплаты Регистрации и информационного обслуживания ПК IKASSA в СККО",
                    path = "skko/invoice.docx",
                    alias = "skko_invoice"
                ),
                Document(
                    name = "04 Реквизиты для оплаты Регистрации и абонентского обслуживания ПК IKASSA",
                    path = "invoice.docx",
                    alias = "invoice"
                ),
                Document(
                    name = "04 Реквизиты для оплаты Регистрации ПК IKASSA",
                    path = "invoice_register.docx",
                    alias = "invoice_register"
                ),
                Document(
                    name = "04 Реквизиты для оплаты абонентского обслуживания ПК IKASSA",
                    path = "invoice_license.docx",
                    alias = "invoice_license"
                ),
                Document(
                    name = "06 Рекомендованная форма доверенности (при оформлении уполномоченным представителем)",
                    path = "attorney.docx",
                    alias = "attorney"
                ),
                Document(
                    name = "07 Уведомление о присоединении к договору",
                    path = "connection_notification.docx",
                    alias = "connection_notification"
                ),
                Document(
                    name = "08 Акт приема-передачи СКО",
                    path = "skko/sko_act.docx",
                    alias = "skko_sko_act"
                ),
                Document(
                    name = "09 Заявление о доступе в Личный Кабинет по паролю",
                    path = "declaration_lk_unsafe.docx",
                    alias = "declaration_lk_unsafe"
                )
            )
        )
    }


}