"use client";

import {
  useEffect,
  useState,
} from "react";

import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";
import { getRole } from "@/lib/auth";

type Product = {
  id: number;
  name: string;
};

type StockMovement = {
  id: number;
  productName: string;
  movementType: string;
  quantity: number;
  createdAt: string;
  performedByEmail: string;
};

export default function StockMovementsPage() {
  const [movements, setMovements] =
    useState<StockMovement[]>([]);

  const [products, setProducts] =
    useState<Product[]>([]);

  const [role, setRole] =
    useState<string | null>(null);

  const [productId, setProductId] =
    useState("");

  const [movementType, setMovementType] =
    useState("ADJUSTMENT_IN");

  const [quantity, setQuantity] =
    useState("");

  const [reason, setReason] =
    useState("");

  useEffect(() => {
    setRole(getRole());

    loadData();
  }, []);

  async function loadData() {
    try {
      const movementData =
        await apiRequest(
          "/api/stock-movements"
        );

      const productData =
        await apiRequest(
          "/api/products"
        );

      setMovements(movementData);
      setProducts(productData);
    } catch (error) {
      console.error(error);
    }
  }

  async function adjustStock(
    e: React.FormEvent
  ) {
    e.preventDefault();

    try {
      await apiRequest(
        "/api/stock-movements/adjust",
        {
          method: "POST",

          body: JSON.stringify({
            productId:
              Number(productId),

            movementType,

            quantity:
              Number(quantity),

            reason,
          }),
        }
      );

      setProductId("");
      setQuantity("");
      setReason("");

      await loadData();
    } catch (error) {
      console.error(error);

      alert(
        "Could not adjust stock"
      );
    }
  }

  const canAdjust =
    role === "ADMIN" ||
    role === "INVENTORY_MANAGER";

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Stock Movements
      </h1>

      <p className="mt-2 text-gray-600">
        View stock changes
      </p>

      {canAdjust && (
        <form
          onSubmit={adjustStock}
          className="mt-6 max-w-xl rounded-lg border bg-white p-5"
        >
          <h2 className="mb-4 text-lg font-bold text-black">
            Adjust Stock
          </h2>

          <select
            value={productId}
            onChange={(e) =>
              setProductId(
                e.target.value
              )
            }
            className="mb-3 w-full rounded border p-2 text-black"
            required
          >
            <option value="">
              Select Product
            </option>

            {products.map(
              (product) => (
                <option
                  key={product.id}
                  value={product.id}
                >
                  {product.name}
                </option>
              )
            )}
          </select>

          <select
            value={movementType}
            onChange={(e) =>
              setMovementType(
                e.target.value
              )
            }
            className="mb-3 w-full rounded border p-2 text-black"
          >
            <option value="ADJUSTMENT_IN">
              Add Stock
            </option>

            <option value="ADJUSTMENT_OUT">
              Remove Stock
            </option>
          </select>

          <input
            type="number"
            placeholder="Quantity"
            value={quantity}
            onChange={(e) =>
              setQuantity(
                e.target.value
              )
            }
            min="1"
            className="mb-3 w-full rounded border p-2 text-black"
            required
          />

          <input
            type="text"
            placeholder="Reason"
            value={reason}
            onChange={(e) =>
              setReason(
                e.target.value
              )
            }
            className="mb-3 w-full rounded border p-2 text-black"
            required
          />

          <button
            type="submit"
            className="rounded bg-blue-600 px-4 py-2 text-white"
          >
            Adjust Stock
          </button>
        </form>
      )}

      <div className="mt-8 overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">
                Product
              </th>

              <th className="p-3">
                Type
              </th>

              <th className="p-3">
                Quantity
              </th>

              <th className="p-3">
                Performed By
              </th>

              <th className="p-3">
                Date
              </th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {movements.map(
              (movement) => (
                <tr
                  key={movement.id}
                  className="border-t"
                >
                  <td className="p-3">
                    {
                      movement.productName
                    }
                  </td>

                  <td className="p-3">
                    {
                      movement.movementType
                    }
                  </td>

                  <td className="p-3">
                    {movement.quantity}
                  </td>

                  <td className="p-3">
                    {
                      movement.performedByEmail
                    }
                  </td>

                  <td className="p-3">
                    {movement.createdAt
                      ? new Date(
                          movement.createdAt
                        ).toLocaleDateString()
                      : "-"}
                  </td>
                </tr>
              )
            )}
          </tbody>
        </table>

        {movements.length === 0 && (
          <p className="p-6 text-gray-500">
            No stock movements found.
          </p>
        )}
      </div>
    </AppLayout>
  );
}