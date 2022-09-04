package kitchenpos.testglue.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import kitchenpos.application.ProductService;
import kitchenpos.application.fixture.ProductMother;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;

@TestGlueConfiguration
public class productTetGlueConfiguration extends TestGlueSupport {

	private final ProductService productService;
	private final ProductRepository productRepository;

	public productTetGlueConfiguration(
		ProductService productService,
		ProductRepository productRepository
	) {
		this.productService = productService;
		this.productRepository = productRepository;
	}

	@TestGlueOperation("{} 데이터를 생성하고")
	public void create_data(String name) {
		Product 상품 = ProductMother.findByName(name);

		put(name, 상품);
	}

	@TestGlueOperation("{} 생성을 요청하면")
	public void create_request(String name) {
		try {
			Product product = productService.create(getAsType(name, Product.class));
			put(name, product);
		} catch (Exception ignore) {
		}
	}

	@TestGlueOperation("{}이 생성된다")
	public void create_success(String name) {
		Product 상품 = getAsType(name, Product.class);
		assertThat(productRepository.findById(상품.getId())).isNotEmpty();
	}

	@TestGlueOperation("{}이 생성에 실패한다")
	public void create_fail(String name) {
		Product 상품 = getAsType(name, Product.class);
		assertThat(상품.getId()).isNull();
	}

	@TestGlueOperation("{} 을 생성하고")
	public void create(String name) {
		Product product = ProductMother.findByName(name);
		put(name, productService.create(product));
	}

	@TestGlueOperation("{} 가격을 {} 으로 변경하면")
	public void changePrice(String name, String price) {
		Product product = getAsType(name, Product.class);
		BigDecimal bigDecimalPrice = toBigDecimal(price);
		product.setPrice(bigDecimalPrice);

		put("changedPrice", bigDecimalPrice);

		try {
			productService.changePrice(product.getId(), product);
		} catch (Exception ignore) {
		}
	}

	@TestGlueOperation("{} 가격은 변경된다")
	public void changePrice_result(String name) {
		Product product = getAsType(name, Product.class);
		Product savedProduct = productRepository.findById(product.getId()).orElseThrow();
		BigDecimal changedPrice = getAsType("changedPrice", BigDecimal.class);

		assertThat(savedProduct.getPrice().longValue()).isEqualTo(changedPrice.longValue());
	}

	@TestGlueOperation("{} 가격 변경에 실패한다")
	public void changePrice_result_fail(String name) {
		Product product = getAsType(name, Product.class);
		Product savedProduct = productRepository.findById(product.getId()).orElseThrow();

		assertThat(savedProduct.getPrice()).isNotEqualTo(product.getPrice());
	}

	@TestGlueOperation("존재하지 않는 상품 가격 변경에 실패한다")
	public void changePrice_no_exist_fail() {

	}

	private BigDecimal toBigDecimal(String price) {
		try {
			return BigDecimal.valueOf(Long.parseLong(price));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
