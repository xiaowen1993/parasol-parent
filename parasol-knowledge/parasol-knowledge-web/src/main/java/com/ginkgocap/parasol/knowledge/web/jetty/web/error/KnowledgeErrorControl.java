package com.ginkgocap.parasol.knowledge.web.jetty.web.error;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class KnowledgeErrorControl {
	public static Logger logger = Logger.getLogger(KnowledgeErrorControl.class);

    /*
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	@ResponseBody
	public ResponseEntity<ResponseError> defaultErrorHandler(HttpServletRequest request, Exception ex) {
		
		ResponseError responseError = new ResponseError();
		HttpStatus status = getStatus(request, ex);
		if (ObjectUtils.equals(status, HttpStatus.BAD_REQUEST)) {
			responseError.setMessage(ex.getLocalizedMessage());
		}
		
		
		if (ObjectUtils.equals(status,HttpStatus.INTERNAL_SERVER_ERROR)){
			ex.printStackTrace(System.err);
			responseError.setMessage(ex.getLocalizedMessage());
			responseError.setError_subcode(getErrorCode(ex));
		}
		
		ResponseEntity<ResponseError> responseEntity = new ResponseEntity<ResponseError>(responseError, status);
		return responseEntity;
	}

	private HttpStatus getStatus(HttpServletRequest request, Exception ex) {
		Integer statusCode = getStatusFromException(ex);
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception e) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	protected Integer getStatusFromException(Exception ex) {
		if (ex instanceof NoSuchRequestHandlingMethodException) {
			return HttpServletResponse.SC_NOT_FOUND;
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			return HttpServletResponse.SC_METHOD_NOT_ALLOWED;
		} else if (ex instanceof HttpMediaTypeNotSupportedException) {
			return HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
		} else if (ex instanceof HttpMediaTypeNotAcceptableException) {
			return HttpServletResponse.SC_NOT_ACCEPTABLE;
		} else if (ex instanceof MissingPathVariableException) {
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} else if (ex instanceof MissingServletRequestParameterException) {
			return HttpServletResponse.SC_BAD_REQUEST;
		} else if (ex instanceof ServletRequestBindingException) {
			return HttpServletResponse.SC_BAD_REQUEST;
		} else if (ex instanceof ConversionNotSupportedException) {
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} else if (ex instanceof TypeMismatchException) {
			return HttpServletResponse.SC_BAD_REQUEST;
		} else if (ex instanceof HttpMessageNotReadableException) {
			return HttpServletResponse.SC_BAD_REQUEST;
		} else if (ex instanceof HttpMessageNotWritableException) {
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} else if (ex instanceof MethodArgumentNotValidException) {
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} else if (ex instanceof MissingServletRequestPartException) {
			return HttpServletResponse.SC_BAD_REQUEST;
		} else if (ex instanceof BindException) {
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		return null;
	}
	
	
	protected int getErrorCode(Exception ex) {
		if (ex != null && ex.getClass().getName().endsWith("ServiceException")) {
			try {
				return (Integer) MethodUtils.invokeMethod(ex, "getErrorCode", null);
			} catch (Exception e) {
			}
		}
		return 0;
	}*/
}
