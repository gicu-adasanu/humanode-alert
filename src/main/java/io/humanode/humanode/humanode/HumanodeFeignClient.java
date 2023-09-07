package io.humanode.humanode.humanode;


import io.humanode.humanode.dtos.BioAuthStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "humanode-feign", url = "${humanode.api}")
public interface HumanodeFeignClient {
    @GetMapping(value = "", produces = "application/json", consumes = "application/json")
    BioAuthStatusDTO getBioAuthStatus(@RequestBody String value);
}
