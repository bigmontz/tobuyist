package bigmontz.tobuyist.entrypoint.rest.advice

import bigmontz.tobuyist.business.usecase.exception.AlreadyExistsException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class AlreadyExistsExceptionMapper : ExceptionMapper<AlreadyExistsException> {
    override fun toResponse(p0: AlreadyExistsException?): Response =
            Response.status(Response.Status.CONFLICT).build()
}