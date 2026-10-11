"use client";

import { useEffect, useState } from "react";
import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Supplier = {
  id: number;
  companyName: string;
};

type PurchaseRequest = {
  id: number;
  requestNumber: string;
  productName: string;
  status: string;
};

type PurchaseOrderItem = {
  id: number;
  productName: string;
  quantityOrdered: number;
  lineTotal: number;
};

type PurchaseOrder = {
  id: number;
  poNumber: string;
  supplierName: string;
  status: string;
  totalCost: number;
  expectedDeliveryDate: string;
  items: PurchaseOrderItem[];
};

export default function PurchaseOrdersPage() {
  const [orders, setOrders] =
    useState<PurchaseOrder[]>([]);

  const [suppliers, setSuppliers] =
    useState<Supplier[]>([]);

  const [requests, setRequests] =
    useState<PurchaseRequest[]>([]);

  const [supplierId, setSupplierId] =
    useState("");

  const [
    purchaseRequestId,
    setPurchaseRequestId,
  ] = useState("");

  const [deliveryDate, setDeliveryDate] =
    useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const orderData =
        await apiRequest(
          "/api/purchase-orders"
        );

      const supplierData =
        await apiRequest(
          "/api/suppliers"
        );

      const requestData =
        await apiRequest(
          "/api/purchase-requests"
        );

      setOrders(orderData);
      setSuppliers(supplierData);

      setRequests(
        requestData.filter(
          (request: PurchaseRequest) =>
            request.status === "APPROVED"
        )
      );
    } catch (error) {
      console.error(error);
    }
  }

  async function createOrder(
    e: React.FormEvent
  ) {
    e.preventDefault();

    try {
      await apiRequest(
        "/api/purchase-orders",
        {
          method: "POST",

          body: JSON.stringify({
            supplierId:
              Number(supplierId),

            expectedDeliveryDate:
              deliveryDate,

            items: [
              {
                purchaseRequestId:
                  Number(
                    purchaseRequestId
                  ),
              },
            ],
          }),
        }
      );

      setSupplierId("");
      setPurchaseRequestId("");
      setDeliveryDate("");

      loadData();
    } catch (error) {
      console.error(error);

      alert(
        "Could not create purchase order"
      );
    }
  }

  async function placeOrder(
    id: number
  ) {
    try {
      await apiRequest(
        `/api/purchase-orders/${id}/order`,
        {
          method: "PATCH",
        }
      );

      loadData();
    } catch (error) {
      console.error(error);

      alert(
        "Could not place purchase order"
      );
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Purchase Orders
      </h1>

      <p className="mt-2 text-gray-600">
        Create and manage purchase orders
      </p>

      {/* CREATE PO */}

      <form
        onSubmit={createOrder}
        className="mt-6 max-w-xl rounded-lg border bg-white p-5"
      >
        <h2 className="mb-4 text-lg font-bold text-black">
          Create Purchase Order
        </h2>

        <select
          value={supplierId}
          onChange={(e) =>
            setSupplierId(
              e.target.value
            )
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
        >
          <option value="">
            Select Supplier
          </option>

          {suppliers.map(
            (supplier) => (
              <option
                key={supplier.id}
                value={supplier.id}
              >
                {
                  supplier.companyName
                }
              </option>
            )
          )}
        </select>

        <select
          value={purchaseRequestId}
          onChange={(e) =>
            setPurchaseRequestId(
              e.target.value
            )
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
        >
          <option value="">
            Select Approved Request
          </option>

          {requests.map((request) => (
            <option
              key={request.id}
              value={request.id}
            >
              {request.requestNumber}
              {" - "}
              {request.productName}
            </option>
          ))}
        </select>

        <input
          type="date"
          value={deliveryDate}
          onChange={(e) =>
            setDeliveryDate(
              e.target.value
            )
          }
          className="mb-3 w-full rounded border p-2 text-black"
        />

        <button
          type="submit"
          className="rounded bg-blue-600 px-4 py-2 text-white"
        >
          Create Order
        </button>
      </form>

      {/* TABLE */}

      <div className="mt-8 overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">
                PO Number
              </th>

              <th className="p-3">
                Supplier
              </th>

              <th className="p-3">
                Cost
              </th>

              <th className="p-3">
                Status
              </th>

              <th className="p-3">
                Action
              </th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {orders.map((order) => (
              <tr
                key={order.id}
                className="border-t"
              >
                <td className="p-3">
                  {order.poNumber}
                </td>

                <td className="p-3">
                  {order.supplierName}
                </td>

                <td className="p-3">
                  ₹{order.totalCost}
                </td>

                <td className="p-3">
                  {order.status}
                </td>

                <td className="p-3">
                  {order.status ===
                    "DRAFT" && (
                    <button
                      onClick={() =>
                        placeOrder(
                          order.id
                        )
                      }
                      className="rounded bg-blue-600 px-3 py-1 text-white"
                    >
                      Place Order
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AppLayout>
  );
}