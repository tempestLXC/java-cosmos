package io.github.thunderz99.cosmos.condition;

import static org.assertj.core.api.Assertions.*;
import static io.github.thunderz99.cosmos.condition.Condition.SubConditionType;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.microsoft.azure.documentdb.SqlParameter;

class ConditionTest {

	@Test
	public void buildQuerySpec_should_get_correct_SQL() {

		var q = Condition.filter("fullName.last", "Hanks", //

				"id", List.of("id001", "id002", "id005"), //
				"age", 30) //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT * FROM c WHERE (c[\"fullName\"][\"last\"] = @param000_fullName__last) AND (c[\"id\"] IN (@param001_id__0, @param001_id__1, @param001_id__2)) AND (c[\"age\"] = @param002_age) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_fullName__last", "Hanks").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_id__0", "id001").toJson());
		assertThat(params.get(2).toJson()).isEqualTo(new SqlParameter("@param001_id__1", "id002").toJson());
		assertThat(params.get(3).toJson()).isEqualTo(new SqlParameter("@param001_id__2", "id005").toJson());
		assertThat(params.get(4).toJson()).isEqualTo(new SqlParameter("@param002_age", 30).toJson());
	}

	@Test
	public void buildQuerySpec_should_get_correct_SQL_for_Count() {

		var q = Condition.filter("fullName.last", "Hanks", //
				"id", List.of("id001", "id002", "id005"), //
				"age", 30) //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpecForCount();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT COUNT(1) FROM c WHERE (c[\"fullName\"][\"last\"] = @param000_fullName__last) AND (c[\"id\"] IN (@param001_id__0, @param001_id__1, @param001_id__2)) AND (c[\"age\"] = @param002_age)");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_fullName__last", "Hanks").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_id__0", "id001").toJson());
		assertThat(params.get(2).toJson()).isEqualTo(new SqlParameter("@param001_id__1", "id002").toJson());
		assertThat(params.get(3).toJson()).isEqualTo(new SqlParameter("@param001_id__2", "id005").toJson());
		assertThat(params.get(4).toJson()).isEqualTo(new SqlParameter("@param002_age", 30).toJson());
	}

	@Test
	public void buildQuerySpec_should_work_for_compare_operator() {

		var q = Condition.filter("fullName.last", "Hanks", //

				"id", List.of("id001", "id002", "id005"), //
				"age >=", 30, //
				"fullName.last !=", "ABC") //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT * FROM c WHERE (c[\"fullName\"][\"last\"] = @param000_fullName__last) AND (c[\"id\"] IN (@param001_id__0, @param001_id__1, @param001_id__2)) AND (c[\"age\"] >= @param002_age) AND (c[\"fullName\"][\"last\"] != @param003_fullName__last) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_fullName__last", "Hanks").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_id__0", "id001").toJson());
		assertThat(params.get(2).toJson()).isEqualTo(new SqlParameter("@param001_id__1", "id002").toJson());
		assertThat(params.get(3).toJson()).isEqualTo(new SqlParameter("@param001_id__2", "id005").toJson());
		assertThat(params.get(4).toJson()).isEqualTo(new SqlParameter("@param002_age", 30).toJson());
		assertThat(params.get(5).toJson()).isEqualTo(new SqlParameter("@param003_fullName__last", "ABC").toJson());
	}

	@Test
	public void buildQuerySpec_should_work_for_str_operator() {

		var q = Condition.filter("fullName.last", "Hanks", //

				"id", List.of("id001", "id002", "id005"), //
				"age", 30, //
				"fullName.first OR fullName.last STARTSWITH", "F", //
				"fullName.last CONTAINS", "L", //
				"skill ARRAY_CONTAINS", "Java") //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT * FROM c WHERE (c[\"fullName\"][\"last\"] = @param000_fullName__last) AND (c[\"id\"] IN (@param001_id__0, @param001_id__1, @param001_id__2)) AND (c[\"age\"] = @param002_age) AND ( (STARTSWITH(c[\"fullName\"][\"first\"], @param003_fullName__first)) OR (STARTSWITH(c[\"fullName\"][\"last\"], @param004_fullName__last)) ) AND (CONTAINS(c[\"fullName\"][\"last\"], @param005_fullName__last)) AND (ARRAY_CONTAINS(c[\"skill\"], @param006_skill)) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_fullName__last", "Hanks").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_id__0", "id001").toJson());
		assertThat(params.get(2).toJson()).isEqualTo(new SqlParameter("@param001_id__1", "id002").toJson());
		assertThat(params.get(3).toJson()).isEqualTo(new SqlParameter("@param001_id__2", "id005").toJson());
		assertThat(params.get(4).toJson()).isEqualTo(new SqlParameter("@param002_age", 30).toJson());
		assertThat(params.get(5).toJson()).isEqualTo(new SqlParameter("@param003_fullName__first", "F").toJson());
		assertThat(params.get(6).toJson()).isEqualTo(new SqlParameter("@param004_fullName__last", "F").toJson());
		assertThat(params.get(7).toJson()).isEqualTo(new SqlParameter("@param005_fullName__last", "L").toJson());
		assertThat(params.get(8).toJson()).isEqualTo(new SqlParameter("@param006_skill", "Java").toJson());
	}

	@Test
	public void buildQuerySpec_should_generate_SQL_for_fields() {

		var q = Condition.filter("fullName.last", "Hanks", //

				"id", List.of("id001", "id002", "id005"), //
				"age", 30) //
				.fields("id", "fullName.first", "age") //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT VALUE {\"id\":c.id, \"fullName\":{\"first\":c.fullName.first}, \"age\":c.age} FROM c WHERE (c[\"fullName\"][\"last\"] = @param000_fullName__last) AND (c[\"id\"] IN (@param001_id__0, @param001_id__1, @param001_id__2)) AND (c[\"age\"] = @param002_age) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_fullName__last", "Hanks").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_id__0", "id001").toJson());
		assertThat(params.get(2).toJson()).isEqualTo(new SqlParameter("@param001_id__1", "id002").toJson());
		assertThat(params.get(3).toJson()).isEqualTo(new SqlParameter("@param001_id__2", "id005").toJson());
		assertThat(params.get(4).toJson()).isEqualTo(new SqlParameter("@param002_age", 30).toJson());
	}

	@Test
	public void generate_field_should_work() {
		assertThat(Condition.generateOneFieldSelect("org.leader.name"))
				.isEqualTo("\"org\":{\"leader\":{\"name\":c.org.leader.name}}");
	}

	@Test
	public void generate_field_should_throw_when_invalid_field() {
		for (var ch : List.of("{", "}", ",", "\"", "'")) {
			assertThatThrownBy(() -> Condition.generateOneFieldSelect(ch)).isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("field cannot").hasMessageContaining(ch);

		}

	}

	@Test
	public void buildQuerySpec_should_work_for_sub_cond_or() {

		var q = Condition.filter("fullName.last", "Hanks", //
				SubConditionType.SUB_COND_OR,
				List.of(Condition.filter("position", "leader"), Condition.filter("organization", "executive")), //
				"age", 30) //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT * FROM c WHERE (c[\"fullName\"][\"last\"] = @param000_fullName__last) AND ((c[\"position\"] = @param001_position) OR (c[\"organization\"] = @param002_organization)) AND (c[\"age\"] = @param003_age) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_fullName__last", "Hanks").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_position", "leader").toJson());
		assertThat(params.get(2).toJson()).isEqualTo(new SqlParameter("@param002_organization", "executive").toJson());
		assertThat(params.get(3).toJson()).isEqualTo(new SqlParameter("@param003_age", 30).toJson());
	}

	@Test
	public void buildQuerySpec_should_work_for_sub_cond_or_from_the_beginning() {

		var q = Condition.filter( //
				SubConditionType.SUB_COND_OR,
				List.of(Condition.filter("position", "leader"), Condition.filter("organization", "executive")) //
		) //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT * FROM c WHERE ((c[\"position\"] = @param000_position) OR (c[\"organization\"] = @param001_organization)) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_position", "leader").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_organization", "executive").toJson());
	}


	@Test
	public void buildQuerySpec_should_work_for_sub_cond_and() {

		var q = Condition.filter( //
				SubConditionType.SUB_COND_AND,
				List.of(Condition.filter("position", "leader"), Condition.filter("organization", "executive")) //
		) //
				.sort("_ts", "DESC") //
				.offset(10) //
				.limit(20) //
				.toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo(
				"SELECT * FROM c WHERE ((c[\"position\"] = @param000_position) AND (c[\"organization\"] = @param001_organization)) ORDER BY c[\"_ts\"] DESC OFFSET 10 LIMIT 20");

		var params = List.copyOf(q.getParameters());

		assertThat(params.get(0).toJson()).isEqualTo(new SqlParameter("@param000_position", "leader").toJson());
		assertThat(params.get(1).toJson()).isEqualTo(new SqlParameter("@param001_organization", "executive").toJson());
	}

	@Test
	public void buildQuerySpec_should_work_for_empty() {

		var q = Condition.filter().toQuerySpec();

		assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c OFFSET 0 LIMIT 100");

		assertThat(q.getParameters()).isEmpty();

	}

	@Test
	public void buildQuerySpec_should_work_for_empty_sub_query() {

		{
			var q = Condition.filter(SubConditionType.SUB_COND_OR, //
					List.of()).toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).isEmpty();
		}

		{
			var q = Condition.filter(SubConditionType.SUB_COND_OR, //
					List.of(Condition.filter("id", 1))).toQuerySpec();

			assertThat(q.getQueryText().trim())
					.isEqualTo("SELECT * FROM c WHERE ((c[\"id\"] = @param000_id)) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(1);
		}

		{
			var q = Condition.filter("id", 1, SubConditionType.SUB_COND_AND, //
					List.of()).toQuerySpec();

			assertThat(q.getQueryText().trim())
					.isEqualTo("SELECT * FROM c WHERE (c[\"id\"] = @param000_id) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(1);
		}

		{
			var q = Condition.filter(SubConditionType.SUB_COND_AND, //
					List.of(Condition.filter(), Condition.filter())).toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(0);
		}

		{
			var q = Condition.filter("id", 1, SubConditionType.SUB_COND_OR, //
					List.of(Condition.filter())).toQuerySpec();

			assertThat(q.getQueryText().trim())
					.isEqualTo("SELECT * FROM c WHERE (c[\"id\"] = @param000_id) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(1);
		}

		{
			var q = Condition.filter("id", 1, SubConditionType.SUB_COND_OR, //
					List.of(Condition.filter(), Condition.filter("name", "Tom"))).toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo(
					"SELECT * FROM c WHERE (c[\"id\"] = @param000_id) AND ((c[\"name\"] = @param001_name)) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(2);
		}

	}

	@Test
	public void buildQuerySpec_should_work_for_raw_cond() {

		{
			var q = Condition.filter("SUB_COND_RAW", //
					"1=0").toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c WHERE (1=0) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).isEmpty();
		}

		{
			var q = Condition.filter("open", true, //
				"SUB_COND_RAW", //
					"1=1").toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c WHERE (c[\"open\"] = @param000_open) AND (1=1) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(1);

		}

	}


	@Test
	public void buildQuerySpec_should_work_empty_list() {

		{
			var q = Condition.filter("id", List.of()).toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c WHERE (1=0) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).isEmpty();
		}

		{
			var q = Condition.filter("id", List.of(), "name", "Tom").toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c WHERE (1=0) AND (c[\"name\"] = @param000_name) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(1);
		}

		{
			var q = Condition.filter("name", "Tom", "id", List.of()).toQuerySpec();

			assertThat(q.getQueryText().trim()).isEqualTo("SELECT * FROM c WHERE (c[\"name\"] = @param000_name) AND (1=0) OFFSET 0 LIMIT 100");
			assertThat(q.getParameters()).hasSize(1);
		}


	}

}