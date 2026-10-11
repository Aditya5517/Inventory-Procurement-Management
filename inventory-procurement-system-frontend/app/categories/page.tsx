"use client";

import { useEffect, useState } from "react";
import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Category = {
  id: number;
  name: string;
  description: string;
  isActive: boolean;
};

export default function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadCategories();
  }, []);

  async function loadCategories() {
    try {
      const data = await apiRequest("/api/categories");
      setCategories(data);
    } catch (error) {
      console.error(error);
      setError("Could not load categories");
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Categories
      </h1>

      <p className="mt-2 text-gray-600">
        Product categories
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
              <th className="p-3">Name</th>
              <th className="p-3">Description</th>
              <th className="p-3">Status</th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {categories.map((category) => (
              <tr
                key={category.id}
                className="border-t"
              >
                <td className="p-3">
                  {category.name}
                </td>

                <td className="p-3">
                  {category.description}
                </td>

                <td className="p-3">
                  {category.isActive
                    ? "Active"
                    : "Inactive"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {categories.length === 0 && (
          <p className="p-6 text-gray-500">
            No categories found.
          </p>
        )}
      </div>
    </AppLayout>
  );
}