package management.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import management.data.docs.Document
import management.data.docs.RenderedDocument
import management.data.products.PartnerForm
import management.data.products.Solution
import management.forms.DocumentDto
import management.services.FillDocumentService
import management.services.PartnerService
import management.services.ProductService
import management.services.SolutionService
import management.utils.ellipsis
import management.utils.morph
import management.utils.notFound
import management.utils.serveFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Controller("/docs")
class DocumentController (
    private val fillDocumentService: FillDocumentService,
    private val productService: ProductService,
    private val solutionService: SolutionService,
    private val partnerService: PartnerService

    ) {

    private val documentsDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy")


    fun step3Common(
        period : Short,
        count: Short,
        solution : Solution = solutionService.getSolutionByAlias("smart")!!,
        partnerForm: PartnerForm = partnerService.getFormByUNP(193141246)!!
//        partnerUNP: Int? = null
    ) : MutableList<RenderedDocument> {

// FIXME: из маркетинга, но я ниче не понял, что здесь происходит
//        val equipment = productService.getAllProducts().mapNotNull {
//            val p = solution.equipment.contains(it.alias)
//            if(
//                p &&
//                period >= 6.toShort() &&
//                //partner?.partnerUnp != null &&
//                //listOf(190374449,193141246).contains(partner.partnerUnp) &&
//                listOf("pax930", "pax930_lancard", "pax930_promo").contains(it.alias)
//            ) {
//                // Преопределяем цену если решение -- пакс
//                // и если партнёр в списке партнёров
//                // OPKI-4223
////                 TODO: выпилить это
//                it.copy(price=productsService.getProduct("pax930_promo")!!.price)
//            } else if(p) {
//                it
//            } else {
//                null
//            }
//        }

        val solutionContent = solution.contents.map { it.alias }
        val renderedDocuments = mutableListOf<RenderedDocument>()

        val billingMode = if (solutionContent.isEmpty() || solutionContent.containsAll(listOf("ikassa_register", "ikassa_license"))) {
            IkassaBillingMode.FULL
        } else if (solutionContent.contains("ikassa_register")) {
            IkassaBillingMode.REGISTER
        } else {
            IkassaBillingMode.LICENSE
        }

        solution.contents.forEach {
            when(it.alias) { "ikassa_register" -> {
                when(billingMode) { IkassaBillingMode.FULL -> {
                        val ikassaInvoice = fillDocumentService.fillIkassaInvoice(
                            count,
                            period,
                            if (solutionContent.contains("dusik_r")) {
                                "_dusik"
                            }
                            else {
                                ""
                            }
                        )
                        ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                        renderedDocuments.add(ikassaInvoice)
                    }

                    IkassaBillingMode.LICENSE -> {
                        val ikassaInvoice = fillDocumentService.fillIkassaRegistration(count)
                        renderedDocuments.add(ikassaInvoice)
                    }

                    IkassaBillingMode.REGISTER -> {
                        val ikassaInvoice = fillDocumentService.fillIkassaTariff(count, period)
                        ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                        renderedDocuments.add(ikassaInvoice)
                    }

                }
            }

            }
        }







        throw NotImplementedError()
    }

    @Get("product/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getDocForProduct(@PathVariable alias : String, @QueryValue(defaultValue = "1") count : Short) : HttpResponse<*> {
        val product = productService.getProductByAlias(alias) ?: return notFound()
        val renderedDocuments = fillDocumentService.fillProductsDocuments(listOf(product), count)
        val productDocument = renderedDocuments.firstOrNull() ?: return notFound()
        return if(renderedDocuments.count() > 1) {
            serveFile(
                fillDocumentService.createZipArchive(renderedDocuments),
                "Документы для ${product.name.ellipsis(50)} от ${LocalDate.now().format(documentsDateFormat)}.zip"
            )
        } else {
            serveFile(
                productDocument.content,
                "${productDocument.name.ellipsis(50)} от ${
                    LocalDate.now().format(documentsDateFormat)
                }.${productDocument.extension}"
            )
        }
    }

    @Post("/step3")
    @Produces("application/zip")
    fun step3Post(@Body documentInfo : DocumentDto) : HttpResponse<*> {
//        val form  = partnerService.getFormByUNP(documentInfo.equipment["partner_unp"]!!.toInt())
//            ?: partnerService.

        val solution = solutionService.getSolutionByAlias(documentInfo.equipment["solution"].toString())
            ?: solutionService.getSolutionByAlias("smart")




        throw NotImplementedError("HOW ARE YOU?")
    }


    @Get("/export/default")
    fun exportDefaultDocs() : List<Document> {
        return fillDocumentService.exportDefaultDocs()
    }

    enum class IkassaBillingMode {
        FULL,
        LICENSE,
        REGISTER
    }

}