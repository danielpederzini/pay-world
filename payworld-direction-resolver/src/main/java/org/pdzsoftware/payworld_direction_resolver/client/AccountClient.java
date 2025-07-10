package org.pdzsoftware.payworld_direction_resolver.client;

import org.pdzsoftware.payworld_direction_resolver.dto.AccountInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account", url = "${feign.client.accounts-api.url-prefix}/api/accounts")
public interface AccountClient {
    @GetMapping("/{key}")
    AccountInfoDTO getInfoByKey(@PathVariable String key);
}
