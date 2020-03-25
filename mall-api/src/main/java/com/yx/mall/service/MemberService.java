package com.yx.mall.service;

import com.yx.mall.bean.Member;
import java.util.List;

public interface MemberService {
    public List<Member> getAll();

    public Member getMember(Member member);

    public String login(Member paramMember, String ip);

    public String weiboLogin(String code,String ip);

    public int saveWeiboMember(Member member);
}
