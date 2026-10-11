"use client";

import { useEffect, useState } from "react";
import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type PurchaseOrderItem = {
  id: number;
  productName: string;
  quantityOrdered: number;
};

type PurchaseOrder = {
  id: number;
  poNumber: string;
  status: string;
  items: PurchaseOrderItem[];
};

type GoodsReceipt = {
  id: number;
  receiptNumber: string;
  poNumber: string;
  receivedByEmail: string;
  receivedAt: string;
  notes: string;
};

export default function GoodsReceiptsPage() {
  const [orders, setOrders] =
    useState<PurchaseOrder[]>([]);

  const [receipts, setReceipts] =
    useState<GoodsReceipt[]>([]);

  const [purchaseOrderId, setPurchaseOrderId] =
    useState("");

  const [purchaseOrderItemId, setPurchaseOrderItemId] =
    useState("");

  const [quantity, setQuantity] =
    useState("");

  const [notes, setNotes] =
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

      const receiptData =
        await apiRequest(
          "/api/goods-receipts"
        );

      setOrders(
        orderData.filter(
          (order: PurchaseOrder) =>
            order.status === "ORDERED" ||
            order.status ===
              "PARTIALLY_RECEIVED"
        )
      );

      setReceipts(receiptData);
    } catch (error) {
      console.error(error);
    }
  }

  const selectedOrder =
    orders.find(
      (order) =>
        order.id ===
        Number(purchaseOrderId)
    );

  async function receiveGoods(
    e: React.FormEvent
  ) {
    e.preventDefault();

    try {
      await apiRequest(
        "/api/goods-receipts",
        {
          method: "POST",

          body: JSON.stringify({
            purchaseOrderId:
              Number(
                purchaseOrderId
              ),

            notes,

            items: [
              {
                purchaseOrderItemId:
                  Number(
                    purchaseOrderItemId
                  ),

                quantityReceived:
                  Number(quantity),
              },
            ],
          }),
        }
      );

      setPurchaseOrderId("");
      setPurchaseOrderItemId("");
      setQuantity("");
      setNotes("");

      loadData();
    } catch (error) {
      console.error(error);

      alert(
        "Could not receive goods"
      );
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Goods Receipts
      </h1>

      <p className="mt-2 text-gray-600">
        Record received goods
      </p>

      {/* RECEIVE FORM */}

      <form
        onSubmit={receiveGoods}
        className="mt-6 max-w-xl rounded-lg border bg-white p-5"
      >
        <h2 className="mb-4 text-lg font-bold text-black">
          Receive Goods
        </h2>

        <select
          value={purchaseOrderId}
          onChange={(e) => {
            setPurchaseOrderId(
              e.target.value
            );

            setPurchaseOrderItemId(
              ""
            );
          }}
          className="mb-3 w-full rounded border p-2 text-black"
          required
        >
          <option value="">
            Select Purchase Order
          </option>

          {orders.map((order) => (
            <option
              key={order.id}
              value={order.id}
            >
              {order.poNumber}
            </option>
          ))}
        </select>

        <select
          value={purchaseOrderItemId}
          onChange={(e) =>
            setPurchaseOrderItemId(
              e.target.value
            )
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
        >
          <option value="">
            Select Product
          </option>

          {selectedOrder?.items.map(
            (item) => (
              <option
                key={item.id}
                value={item.id}
              >
                {item.productName}
                {" - Ordered: "}
                {item.quantityOrdered}
              </option>
            )
          )}
        </select>

        <input
          type="number"
          placeholder="Quantity Received"
          value={quantity}
          onChange={(e) =>
            setQuantity(
              e.target.value
            )
          }
          className="mb-3 w-full rounded border p-2 text-black"
          min="1"
          required
        />

        <input
          type="text"
          placeholder="Notes"
          value={notes}
          onChange={(e) =>
            setNotes(e.target.value)
          }
          className="mb-3 w-full rounded border p-2 text-black"
        />

        <button
          type="submit"
          className="rounded bg-blue-600 px-4 py-2 text-white"
        >
          Receive Goods
        </button>
      </form>

      {/* RECEIPTS TABLE */}

      <div className="mt-8 overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">
                Receipt
              </th>

              <th className="p-3">
                Purchase Order
              </th>

              <th className="p-3">
                Received By
              </th>

              <th className="p-3">
                Date
              </th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {receipts.map(
              (receipt) => (
                <tr
                  key={receipt.id}
                  className="border-t"
                >
                  <td className="p-3">
                    {
                      receipt.receiptNumber
                    }
                  </td>

                  <td className="p-3">
                    {receipt.poNumber}
                  </td>

                  <td className="p-3">
                    {
                      receipt.receivedByEmail
                    }
                  </td>

                  <td className="p-3">
                    {receipt.receivedAt
                      ? new Date(
                          receipt.receivedAt
                        ).toLocaleDateString()
                      : "-"}
                  </td>
                </tr>
              )
            )}
          </tbody>
        </table>
      </div>
    </AppLayout>
  );
}