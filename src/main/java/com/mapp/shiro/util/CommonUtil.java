package com.mapp.shiro.util;

import cn.hutool.json.JSONUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CommonUtil {

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return true;
        } else {
            return false;
        }
    }

    public static void ajaxSuccess(HttpServletResponse response, int code, String msg) {
        HashMap<String, Object> res = new LinkedHashMap<>();
        res.put("code", code);
        res.put("msg", msg);
        res.put("success", true);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(code);
        try (Writer writer = response.getWriter()) {
            writer.write(JSONUtil.toJsonStr(res));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ajaxFail(HttpServletResponse response, int code, String msg) {
        HashMap<String, Object> res = new LinkedHashMap<>();
        res.put("code", code);
        res.put("msg", msg);
        res.put("success", false);

        response.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(code);
        try (Writer writer = response.getWriter()) {
            writer.write(JSONUtil.toJsonStr(res));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
