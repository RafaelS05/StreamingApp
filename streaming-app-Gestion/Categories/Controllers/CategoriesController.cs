using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using streaming_app_Gestion.Categories.Service;
using streaming_app_Gestion.Categories.DTO;

namespace streaming_app_Gestion.Categories.Controllers
{
    [ApiController]
    [Route("api/v1/[controller]")]
    public class CategoriesController : Controller
    {
        private readonly ICategoryService _categoriesService;

        public CategoriesController(ICategoryService categoriesService)
        {
            _categoriesService = categoriesService;
        }

        [HttpGet]
        // GET: CategoryController
        //Asigno el valor de task ya que se debe ejecutar una "tarea"
        //Con el ActionResult<List para generar una lista en base a
        //Un objeto deaseado en este caso el CategoryResponse
        public async Task<ActionResult<List<CategoryResponseDto>>> CategoriesList(CancellationToken ct)
        {
            //Creo la variable sobre la cual llamar el servicio que tiene capa de servicio para obtener lo deaseado, esto aplica
            //Sobre todos los metodos que tiene la interfaz
            var category = _categoriesService.GetAllCategoriesAsync(ct);

            //Valida que las categories no sean null o si no encuentra
            //De esto devuelve el OK con el contenido de categories el cual llama el metodo de GetAll
            return category is null ? NotFound() : Ok(category);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CategoryResponseDto>> CategoriesID(int id, CancellationToken ct)
        {
            var category = await _categoriesService.GetCategoryByIdAsync(id, ct);

            return category is null ? NotFound() : Ok(category);
        }

        // GET: CategoryController/Create
        public async Task<ActionResult<CategoryCreateDto>> CreateCategories(CancellationToken ct)
        {
            var category = await _categoriesService.CreateCategoryAsync(ct);

        }

        // POST: CategoryController/Create
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create(IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: CategoryController/Edit/5
        public ActionResult Edit(int id)
        {
            return View();
        }

        // POST: CategoryController/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit(int id, IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: CategoryController/Delete/5
        public ActionResult Delete(int id)
        {
            return View();
        }

        // POST: CategoryController/Delete/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Delete(int id, IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }
    }
}
