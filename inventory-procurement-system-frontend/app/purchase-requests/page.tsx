"use client";

import { useEffect, useState } from "react";
import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Product = {
  id: number;
  name: string;
};

type PurchaseRequest = {
  id: number;
  requestNumber: string;
  productId: number;
  productName: string;
  quantity: number;
  businessReason: string;
  requesterEmail: string;
  status: string;
  neededByDate: string;
  rejectionNote: string | null;
};

export default function PurchaseRequestsPage() {
  const [requests, setRequests] = useState<PurchaseRequest[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  const [productId, setProductId] = useState("");
  const [quantity, setQuantity] = useState("");
  const [reason, setReason] = useState("");
  const [neededDate, setNeededDate] = useState("");

  const [message, setMessage] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const requestData =
        await apiRequest("/api/purchase-requests");

      const productData =
        await apiRequest("/api/products");

      setRequests(requestData);
      setProducts(productData);
    } catch (error) {
      console.error(error);
    }
  }

  async function createRequest(
    e: React.FormEvent
  ) {
    e.preventDefault();

    try {
      await apiRequest(
        "/api/purchase-requests",
        {
          method: "POST",
          body: JSON.stringify({
            productId: Number(productId),
            quantity: Number(quantity),
            businessReason: reason,
            neededByDate: neededDate,
          }),
        }
      );

      setMessage(
        "Purchase request created"
      );

      setProductId("");
      setQuantity("");
      setReason("");
      setNeededDate("");

      loadData();
    } catch (error) {
      console.error(error);

      setMessage(
        "Could not create request"
      );
    }
  }

  async function updateStatus(
    id: number,
    status: string
  ) {
    let rejectionNote = "";

    if (status === "REJECTED") {
      rejectionNote =
        prompt(
          "Enter rejection reason"
        ) || "";

      if (!rejectionNote) {
        return;
      }
    }

    try {
      let endpoint =
        `/api/purchase-requests/${id}/status?status=${status}`;

      if (rejectionNote) {
        endpoint +=
          `&rejectionNote=${encodeURIComponent(
            rejectionNote
          )}`;
      }

      await apiRequest(endpoint, {
        method: "PATCH",
      });

      loadData();
    } catch (error) {
      console.error(error);
      alert("Could not update request");
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Purchase Requests
      </h1>

      <p className="mt-2 text-gray-600">
        Create and review purchase requests
      </p>

      {/* CREATE FORM */}

      <form
        onSubmit={createRequest}
        className="mt-6 max-w-xl rounded-lg border bg-white p-5"
      >
        <h2 className="mb-4 text-lg font-bold text-black">
          Create Request
        </h2>

        <select
          value={productId}
          onChange={(e) =>
            setProductId(e.target.value)
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
        >
          <option value="">
            Select Product
          </option>

          {products.map((product) => (
            <option
              key={product.id}
              value={product.id}
            >
              {product.name}
            </option>
          ))}
        </select>

        <input
          type="number"
          placeholder="Quantity"
          value={quantity}
          onChange={(e) =>
            setQuantity(e.target.value)
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
          min="1"
        />

        <input
          type="text"
          placeholder="Business reason"
          value={reason}
          onChange={(e) =>
            setReason(e.target.value)
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
        />

        <input
          type="date"
          value={neededDate}
          onChange={(e) =>
            setNeededDate(e.target.value)
          }
          className="mb-3 w-full rounded border p-2 text-black"
          required
        />

        <button
          type="submit"
          className="rounded bg-blue-600 px-4 py-2 text-white"
        >
          Create Request
        </button>

        {message && (
          <p className="mt-3 text-sm text-gray-600">
            {message}
          </p>
        )}
      </form>

      {/* TABLE */}

      <div className="mt-8 overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">
                Request
              </th>

              <th className="p-3">
                Product
              </th>

              <th className="p-3">
                Quantity
              </th>

              <th className="p-3">
                Requested By
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
            {requests.map((request) => (
              <tr
                key={request.id}
                className="border-t"
              >
                <td className="p-3">
                  {request.requestNumber}
                </td>

                <td className="p-3">
                  {request.productName}
                </td>

                <td className="p-3">
                  {request.quantity}
                </td>

                <td className="p-3">
                  {request.requesterEmail}
                </td>

                <td className="p-3">
                  {request.status}
                </td>

                <td className="p-3">
                  {request.status ===
                    "PENDING" && (
                    <div className="flex gap-2">
                      <button
                        onClick={() =>
                          updateStatus(
                            request.id,
                            "APPROVED"
                          )
                        }
                        className="rounded bg-green-600 px-3 py-1 text-white"
                      >
                        Approve
                      </button>

                      <button
                        onClick={() =>
                          updateStatus(
                            request.id,
                            "REJECTED"
                          )
                        }
                        className="rounded bg-red-600 px-3 py-1 text-white"
                      >
                        Reject
                      </button>
                    </div>
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