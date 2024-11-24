/*
 * InvestBook
 * Copyright (C) 2024  Spacious Team <spacious-team@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.investbook.web.forms.model;

import org.junit.jupiter.api.Test;
import org.spacious_team.broker.pojo.CashFlowType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EventCashFlowModelTest {

    @Test
    void testDefaultValues() {
        EventCashFlowModel model = new EventCashFlowModel();

        assertNotNull(model.getDate());
        assertEquals(LocalDate.now(), model.getDate());

        assertNotNull(model.getTime());
        assertEquals(LocalTime.NOON, model.getTime());

        assertNotNull(model.getValueCurrency());
        assertEquals("RUB", model.getValueCurrency());
    }

    @Test
    void testSetValueCurrency() {
        EventCashFlowModel model = new EventCashFlowModel();
        model.setValueCurrency("usd");
        assertEquals("USD", model.getValueCurrency());
    }

    @Test
    void testIsValuePositive() {
        EventCashFlowModel model = new EventCashFlowModel();
        model.setValue(BigDecimal.TEN);
        assertTrue(model.getValue().compareTo(BigDecimal.ZERO) >= 0);

        model.setValue(BigDecimal.valueOf(-10));
        assertFalse(model.getValue().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testGetStringType() {
        EventCashFlowModel model = new EventCashFlowModel();
        model.setType(CashFlowType.DIVIDEND);
        assertEquals("DIVIDEND", model.getStringType());

        model.setType(CashFlowType.CASH);
        model.setValue(BigDecimal.TEN);
        model.setDescription("Deposit");
        assertEquals("CASH_IN", model.getStringType());

        model.setValue(BigDecimal.valueOf(-10));
        assertEquals("CASH_OUT", model.getStringType());

        model.setDescription("вычет ИИС");
        assertEquals("TAX_IIS_A", model.getStringType());
    }

    @Test
    void testSetStringType() {
        EventCashFlowModel model = new EventCashFlowModel();
        model.setStringType("TAX_IIS_A");
        assertEquals(CashFlowType.CASH, model.getType());

        model.setStringType("DIVIDEND");
        assertEquals(CashFlowType.DIVIDEND, model.getType());
    }

    @Test
    void testIsAttachedToSecurity() {
        EventCashFlowModel model = new EventCashFlowModel();
        EventCashFlowModel.AttachedSecurity security = model.new AttachedSecurity();

        security.setSecurity("Some Security");
        security.setCount(5);
        model.setType(CashFlowType.DIVIDEND);

        model.setAttachedSecurity(security);
        assertTrue(model.isAttachedToSecurity());
    }

    @Test
    void testAttachedSecurityIsValid() {
        EventCashFlowModel model = new EventCashFlowModel();
        EventCashFlowModel.AttachedSecurity security = model.new AttachedSecurity();

        // Invalid when empty
        assertFalse(security.isValid());

        // Valid setup
        security.setSecurity("Some Security");
        security.setCount(5);
        model.setType(CashFlowType.DIVIDEND);
        assertTrue(security.isValid());

        // Invalid when count is not positive
        security.setCount(0);
        assertFalse(security.isValid());
    }

    @Test
    void testAttachedSecurityGetSecurityName() {
        EventCashFlowModel model = new EventCashFlowModel();
        EventCashFlowModel.AttachedSecurity security = model.new AttachedSecurity();

        model.setType(CashFlowType.DIVIDEND);
        security.setSecurity("Some Security (ISIN1234)");

        String name = security.getSecurityName();
        assertNotNull(name);
        assertTrue(name.contains("Some Security"));
    }
}
