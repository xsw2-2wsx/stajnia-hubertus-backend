package net.jupw.hubertus.api.validation

import org.springframework.core.io.Resource
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [ResourceLengthValidator::class])
annotation class ResourceLength(
    val maxLengthBytes: Long,
    val message: String,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class ResourceLengthValidator : ConstraintValidator<ResourceLength, Resource> {
    private lateinit var annotation: ResourceLength

    override fun initialize(constraintAnnotation: ResourceLength?) {
        annotation = constraintAnnotation?: throw IllegalStateException()
    }

    override fun isValid(resource: Resource, context: ConstraintValidatorContext): Boolean =
        resource.contentLength() <= annotation.maxLengthBytes
}
