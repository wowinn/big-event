package com.yangzhou.validation;

import com.yangzhou.anno.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StateValidation implements ConstraintValidator<State, String> {

    /**
     *
     * @param value 将来要校验的数据
     * @param context context in which the constraint is evaluated
     * @return false:失败 true:成功
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //提供校验规则
        if (value == null)
            return false;
        return value.equals("已发布") || value.equals("草稿");
    }
}
