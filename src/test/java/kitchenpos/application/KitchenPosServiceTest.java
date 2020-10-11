package kitchenpos.application;

import static kitchenpos.constants.Constants.TEST_MENU_GROUP_NAME;
import static kitchenpos.constants.Constants.TEST_MENU_NAME;
import static kitchenpos.constants.Constants.TEST_MENU_PRODUCT_QUANTITY;
import static kitchenpos.constants.Constants.TEST_ORDER_TABLE_EMPTY_FALSE;
import static kitchenpos.constants.Constants.TEST_ORDER_TABLE_EMPTY_TRUE;
import static kitchenpos.constants.Constants.TEST_ORDER_TABLE_NUMBER_OF_GUESTS;
import static kitchenpos.constants.Constants.TEST_ORDER_TABLE_NUMBER_OF_GUESTS_EMPTY;
import static kitchenpos.constants.Constants.TEST_PRODUCT_NAME;
import static kitchenpos.constants.Constants.TEST_PRODUCT_PRICE;
import static kitchenpos.constants.Constants.TEST_TABLE_GROUP_CREATED_DATE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import kitchenpos.application.dao.MockMenuDao;
import kitchenpos.application.dao.MockMenuGroupDao;
import kitchenpos.application.dao.MockMenuProductDao;
import kitchenpos.application.dao.MockOrderDao;
import kitchenpos.application.dao.MockOrderLineItemDao;
import kitchenpos.application.dao.MockOrderTableDao;
import kitchenpos.application.dao.MockProductDao;
import kitchenpos.application.dao.MockTableGroupDao;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public abstract class KitchenPosServiceTest {

    protected OrderDao orderDao = new MockOrderDao();
    protected OrderTableDao orderTableDao = new MockOrderTableDao();
    protected ProductDao productDao = new MockProductDao();
    protected TableService tableService = new TableService(orderDao, orderTableDao);
    protected ProductService productService = new ProductService(productDao);
    private TableGroupDao tableGroupDao = new MockTableGroupDao();
    protected TableGroupService tableGroupService
        = new TableGroupService(orderDao, orderTableDao, tableGroupDao);
    private MenuGroupDao menuGroupDao = new MockMenuGroupDao();
    protected MenuGroupService menuGroupService = new MenuGroupService(menuGroupDao);
    private MenuDao menuDao = new MockMenuDao();
    private MenuProductDao menuProductDao = new MockMenuProductDao();
    protected MenuService menuService
        = new MenuService(menuDao, menuGroupDao, menuProductDao, productDao);
    private OrderLineItemDao orderLineItemDao = new MockOrderLineItemDao();
    protected OrderService orderService
        = new OrderService(menuDao, orderDao, orderLineItemDao, orderTableDao);

    protected void setCreatedTableGroup(List<OrderTable> orderTables) {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(TEST_TABLE_GROUP_CREATED_DATE);
        tableGroup.setOrderTables(orderTables);

        tableGroupService.create(tableGroup);
    }

    protected long getCreatedEmptyOrderTableId() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(TEST_ORDER_TABLE_EMPTY_TRUE);
        orderTable.setNumberOfGuests(TEST_ORDER_TABLE_NUMBER_OF_GUESTS_EMPTY);
        OrderTable createdOrderTable = tableService.create(orderTable);

        Long createdOrderTableId = createdOrderTable.getId();
        assertThat(createdOrderTable).isNotNull();
        return createdOrderTableId;
    }

    protected long getCreatedNotEmptyOrderTableId() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(TEST_ORDER_TABLE_EMPTY_FALSE);
        orderTable.setNumberOfGuests(TEST_ORDER_TABLE_NUMBER_OF_GUESTS);
        OrderTable createdOrderTable = tableService.create(orderTable);

        Long createdOrderTableId = createdOrderTable.getId();
        assertThat(createdOrderTable).isNotNull();
        return createdOrderTableId;
    }

    protected long getCreatedMenuGroupId() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(TEST_MENU_GROUP_NAME);
        MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        Long createdMenuGroupId = createdMenuGroup.getId();
        assertThat(createdMenuGroupId).isNotNull();
        return createdMenuGroupId;
    }

    protected long getCreatedProductId() {
        Product product = new Product();
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(TEST_PRODUCT_PRICE);
        Product createdProduct = productService.create(product);

        Long createdProductId = createdProduct.getId();
        assertThat(createdProductId).isNotNull();
        return createdProductId;
    }

    protected long getCreatedMenuId() {
        MenuProduct menuProduct = getMenuProduct();

        Menu menu = new Menu();
        menu.setName(TEST_MENU_NAME);
        menu.setPrice(getMenuProductPrice(menuProduct));
        menu.setMenuGroupId(getCreatedMenuGroupId());
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        Menu createdMenu = menuService.create(menu);

        Long createdMenuId = createdMenu.getId();
        assertThat(createdMenuId).isNotNull();
        return createdMenuId;
    }

    protected BigDecimal getMenuProductPrice(MenuProduct menuProduct) {
        BigDecimal productPrice = productDao.findById(menuProduct.getProductId())
            .orElseThrow(() -> new IllegalArgumentException(
                menuProduct.getMenuId() + "ID에 해당하는 Product가 없습니다."))
            .getPrice();
        BigDecimal quantity = BigDecimal.valueOf(menuProduct.getQuantity());
        return productPrice.multiply(quantity);
    }

    protected MenuProduct getMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(TEST_MENU_PRODUCT_QUANTITY);
        menuProduct.setProductId(getCreatedProductId());
        return menuProduct;
    }
}