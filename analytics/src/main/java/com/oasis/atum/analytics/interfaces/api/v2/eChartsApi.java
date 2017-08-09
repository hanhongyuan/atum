package com.oasis.atum.analytics.interfaces.api.v2;


import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("v2")
public class eChartsApi
{
	private final JdbcTemplate template;

	@PostMapping("test")
	public int test()
	{
		return template.queryForList("SELECT * FROM crm_serve_order WHERE status IN (3502,3601)").size();
	}
}
