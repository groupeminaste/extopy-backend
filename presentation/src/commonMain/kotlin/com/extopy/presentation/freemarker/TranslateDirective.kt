package com.extopy.presentation.freemarker

import com.extopy.domain.services.TranslateService
import freemarker.core.Environment
import freemarker.template.*

/**
 * The `<@t key="..." args=[...] />` directive of the templates. The language comes from the `locale`
 * variable every response puts in its model.
 */
class TranslateDirective(
    private val translateService: TranslateService,
) : TemplateDirectiveModel {

    override fun execute(
        env: Environment?,
        params: MutableMap<Any?, Any?>?,
        loopVars: Array<out TemplateModel>?,
        body: TemplateDirectiveBody?,
    ) {
        val key = params?.get("key") as? TemplateScalarModel
            ?: throw TemplateModelException("The `t` directive requires a `key` parameter")
        val language = (params["locale"] as? TemplateScalarModel)
            ?: (env?.getVariable("locale") as? TemplateScalarModel)
        val args = (params["args"] as? TemplateSequenceModel)?.let { sequence ->
            (0 until sequence.size()).map { sequence[it].toString() }
        } ?: emptyList()

        env?.out?.write(
            translateService.translate(key.asString, language?.asString ?: DEFAULT_LANGUAGE, args)
        )
    }

    companion object {

        private const val DEFAULT_LANGUAGE = "en"

    }

}
