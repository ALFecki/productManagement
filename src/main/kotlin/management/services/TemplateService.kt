package management.services

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import management.forms.FancyMailBodyFragmentDto
import management.utils.replaceMultiple
import org.apache.commons.io.IOUtils
import java.io.StringWriter
import java.time.Year


@Singleton
class TemplateService (

    @Inject
    private val objectMapper : ObjectMapper

    ) {
    fun renderFancyMessageFragment(fragment: FancyMailBodyFragmentDto): String {
        val tpl = (when {
            fragment.templateFile != null -> loadTemplate(fragment.templateFile!!)
            fragment.templateContent != null -> renderFancyMessageFragment(fragment.templateContent!!)
            fragment.template != null -> fragment.template!!
            else -> "<!-- cannot find template -->"
        })

        val renderedVars = hashMapOf<String, String>()
        fragment.vars?.forEach {
            val v = if (it.value == it.value.toString()) {
                it.value.toString()
            } else {
                renderFancyMessageFragment(objectMapper.convertValue(it.value, FancyMailBodyFragmentDto::class.java))
            }
            renderedVars[it.key] = v
        }

        return tpl.replaceMultiple(renderedVars).replaceMultiple(hashMapOf(
            "{year}" to Year.now().toString()/*,
            "{api_url}" to apiURL,
            "{lk_url}" to lkURL,
            "{main_page_url}" to mainPageURL*/
        ))
    }


    fun loadTemplate(name: String): String {
        val templateStream = this::class.java.classLoader.getResourceAsStream("templates/$name.html")
        val writer = StringWriter()
        IOUtils.copy(templateStream, writer, "UTF-8")
        return writer.toString()
    }

}

