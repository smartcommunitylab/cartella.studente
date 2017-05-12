import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'experiencefilter',
    pure: false
})
export class ExperienceFilterPipe implements PipeTransform {
  transform(items: any[], filter: any,field:string): any[] {
    if (!items || !filter) {
      return items;
    }
    // filter items array, items which match and return true will be kept, false will be filtered out
    return items.filter((item: any) => this.applyFilter(item, filter,field));
  }

  /**
   * Perform the filtering.
   *
   * @param {Book} experience The experience to compare to the filter.
   * @param {Book} filter The filter to apply.
   * @return {boolean} True if experience satisfies filters, false if not.
   */
  applyFilter(experience: any, filter: any, field): boolean {
   // for (let field in filter) {
      if (experience.attributes[field]) {
        if (typeof experience.attributes[field] === 'string') {
          if (experience.attributes[field].toLowerCase().indexOf(filter) === -1) {
            return false;
          }
        } else if (typeof experience.attributes[field] === 'number') {
          if (experience.attributes[field] !== filter) {
            return false;
          }
        }
      }
    //}
    return true;
  }
}
