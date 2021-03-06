/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dapeng.cookie;


import com.github.dapeng.router.*;
import com.github.dapeng.router.condition.Condition;
import com.github.dapeng.router.token.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.dapeng.router.RoutesLexer.*;

/**
 * 描述: 语法, 路由规则解析
 * <pre>
 * rules :  (rule eol)*
 * rule  : left '=>' right
 * left  : 'otherwise' matcher (';' matcher)*
 * matcher : id 'match' patterns
 * patterns: pattern (',' pattern)*
 * pattern : '~' pattern
 * | string
 * | regexpString
 * | rangeString
 * | number
 * | ip
 * | kv
 * | mod
 * right : rightPattern (',' rightPattern)*
 * rightPattern : '~' rightPattern | 'c(' string '#' string ')'
 * </pre>
 *
 * @author hz.lei
 * @date 2018年04月13日 下午9:34
 */
public class CookieParser extends RoutesParser {

    public CookieParser(RoutesLexer lexer) {
        super(lexer);
    }

    /**
     * 第一步： 多行规则，根据回车符 ' \n '  进行split  do while 解析
     */
    public List<CookieRule> cookieRoutes() {
        List<CookieRule> routes = new ArrayList<>();
        Token token = lexer.peek();
        switch (token.type()) {
            case Token.EOL:
            case Token.OTHERWISE:
            case Token.ID:
                CookieRule route = cookieRoute();
                if (route != null) {
                    routes.add(route);
                }
                while (lexer.peek() == Token_EOL) {
                    lexer.next(Token.EOL);
                    CookieRule route1 = cookieRoute();
                    if (route1 != null) {
                        routes.add(route1);
                    }
                }
                break;
            case Token.EOF:
                warn("current service hava no route express config");
                break;
            default:
                error("expect `otherwise` or `id match ...` but got " + token);
        }
        return routes;
    }

    /**
     * 解析一条规则，形如:
     * route  : left '=>' right
     * <p>
     * method match s'getFoo'  => ~c'a#b'
     */
    public CookieRule cookieRoute() {
        Token token = lexer.peek();
        switch (token.type()) {
            case Token.OTHERWISE:
            case Token.ID:
                Condition left = left();
                lexer.next(Token.THEN);
                List<CookieRight> right = cookieRiget();
                return new CookieRule(left, right);
            default:
                warn("expect `otherwise` or `id match ...` but got " + token);
        }
        return null;
    }


    /**
     * right : rightPattern (',' rightPattern)*
     * rightPattern : '~' rightPattern
     * | 'c(' string '#' string ')'
     */
    protected List<CookieRight> cookieRiget() {
        List<CookieRight> cookieInfoList = new ArrayList<>();

        Token token = lexer.peek();
        switch (token.type()) {
            case Token.COOKIE:
                CookieToken ct = (CookieToken) lexer.next(Token.COOKIE);
                cookieInfoList.add(new CookieRight(ct.getCookieKey(), ct.getCookieValue()));
                // => ip"" ,
                // => 后 只会跟三种  Token_EOF(结束符号)  Token_COMMA(逗号) EOL(换行符)
                validate(lexer.peek(), Token_COMMA, Token_EOF, Token_EOL);
                while (lexer.peek() == Token_COMMA) {
                    lexer.next(Token.COMMA);
                    CookieToken ct2 = (CookieToken) lexer.next(Token.COOKIE);
                    cookieInfoList.add(new CookieRight(ct2.getCookieKey(), ct2.getCookieValue()));
                }
                return cookieInfoList;
            default:
                error("expect 'c' cookie token , but got:" + token);
                return null;
        }
    }
}
