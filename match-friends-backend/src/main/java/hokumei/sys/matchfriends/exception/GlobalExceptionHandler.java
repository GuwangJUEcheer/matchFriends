package hokumei.sys.matchfriends.exception;

import hokumei.sys.matchfriends.common.BaseResponse;
import hokumei.sys.matchfriends.common.ErrorCode;
import hokumei.sys.matchfriends.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //根据注解针对什么异常进行什么处理
    @ExceptionHandler(value = BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(), e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    //根据注解针对什么异常进行什么处理
    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse businessExceptionHandler(RuntimeException e) {
      log.error("RunTimeException",e);
      return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }
}
