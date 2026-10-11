"use client";

import { useEffect, useState } from "react";
import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Product = {
  id: number;
  sku: string;
  name: string;
  categoryName: string;
  supplierName: string;
  unitCost: number;
  currentStock: number;
  reorderLevel: number;
  isActive: boolean;
};

export default function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadProducts();
  }, []);

  async function loadProducts() {
    try {
      const data = await apiRequest("/api/products");
      setProducts(data);
    } catch (error) {
      console.error(error);
      setError("Could not load products");
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Products
      </h1>

      <p className="mt-2 text-gray-600">
        View inventory products
      </p>

      {error && (
        <p className="mt-4 text-red-600">
          {error}
        </p>
      )}

      <div className="mt-6 overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">SKU</th>
              <th className="p-3">Product</th>
              <th className="p-3">Category</th>
              <th className="p-3">Supplier</th>
              <th className="p-3">Cost</th>
              <th className="p-3">Stock</th>
              <th className="p-3">Status</th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {products.map((product) => (
              <tr
                key={product.id}
                className="border-t"
              >
                <td className="p-3">
                  {product.sku}
                </td>

                <td className="p-3">
                  {product.name}
                </td>

                <td className="p-3">
                  {product.categoryName}
                </td>

                <td className="p-3">
                  {product.supplierName}
                </td>

                <td className="p-3">
                  ₹{product.unitCost}
                </td>

                <td className="p-3">
                  {product.currentStock}
                </td>

                <td className="p-3">
                  {product.currentStock <
                  product.reorderLevel ? (
                    <span className="text-red-600">
                      Low Stock
                    </span>
                  ) : (
                    <span className="text-green-600">
                      Available
                    </span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {products.length === 0 && (
          <p className="p-6 text-gray-500">
            No products found.
          </p>
        )}
      </div>
    </AppLayout>
  );
}