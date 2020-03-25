package com.yx.mall.service;

import com.yx.mall.bean.MemberReceiveAddress;
import java.util.List;

public interface MemberReceiveAddressService {
    public List<MemberReceiveAddress> getMemberReceiveAddressListByMemberId(Long memberId);

    public MemberReceiveAddress getOneMemberReceiveAddress(MemberReceiveAddress memberReceiveAddress);
}