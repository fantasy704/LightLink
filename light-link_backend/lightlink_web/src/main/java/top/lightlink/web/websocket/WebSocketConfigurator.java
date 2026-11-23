package top.lightlink.web.websocket;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import top.lightlink.common.Constants;

import java.util.List;
import java.util.Map;

public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, List<String>> headers = request.getHeaders();
        Map<String, List<String>> cookies = parseCookieHeader(headers.get("cookie"));

        sec.getUserProperties().put("headers", headers);
        sec.getUserProperties().put("cookies", cookies);

        String token = null;

        // 1. 从 URL 查询参数获取（最可靠，推荐前端用这种方式）
        String queryString = request.getRequestURI().getQuery();
        if (queryString != null) {
            for (String param : queryString.split("&")) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2 && kv[0].equals(Constants.TOKEN_USER)) {
                    token = kv[1];
                    break;
                }
            }
        }

        // 2. 从 Header 获取（非浏览器客户端可用）
        if (token == null) {
            List<String> tokenHeader = headers.get(Constants.TOKEN_USER);
            if (tokenHeader != null && !tokenHeader.isEmpty()) {
                token = tokenHeader.get(0);
            }
        }

        // 3. 从 Cookie 获取
        if (token == null) {
            List<String> tokenCookie = cookies.get(Constants.TOKEN_USER);
            if (tokenCookie != null && !tokenCookie.isEmpty()) {
                token = tokenCookie.get(0);
            }
        }

        if (token != null) {
            sec.getUserProperties().put(Constants.TOKEN_USER, token);
        }
    }

    private Map<String, List<String>> parseCookieHeader(List<String> cookieHeaders) {
        Map<String, List<String>> cookies = new java.util.HashMap<>();
        if (cookieHeaders == null) return cookies;
        for (String header : cookieHeaders) {
            String[] pairs = header.split(";");
            for (String pair : pairs) {
                String[] kv = pair.trim().split("=", 2);
                if (kv.length == 2) {
                    cookies.computeIfAbsent(kv[0].toLowerCase(), k -> new java.util.ArrayList<>()).add(kv[1]);
                }
            }
        }
        return cookies;
    }
}
