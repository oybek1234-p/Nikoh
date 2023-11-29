package com.uz.nikoh.location.ipaddress;

import com.uz.nikoh.location.ipaddress.IpAddressInfo;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IpService {
    @GET("/json")
    Call<IpAddressInfo> getIpInfo();
}