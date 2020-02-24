package bigmontz.tobuyist.entrypoint.rest.advice

import bigmontz.tobuyist.business.usecase.exception.ResourceNotFound
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider


@Provider
class ResourceNotFoundExceptionMapper : ExceptionMapper<ResourceNotFound> {

    override fun toResponse(ignored: ResourceNotFound?): Response =
            Response.status(Response.Status.NOT_FOUND).build()
}